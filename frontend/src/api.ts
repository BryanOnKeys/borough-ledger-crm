export type SessionUser = { email: string; displayName: string; role: 'ADMIN'|'MANAGER'|'SALES'|'VIEWER' }
export type Row = Record<string, unknown> & { id: string }
export type Page<T> = { content: T[]; number: number; size: number; totalElements: number; totalPages: number; first?: boolean; last?: boolean }
export type Summary = { clientes: number; contactos: number; prospectos: number; oportunidadesAbiertas: number; oportunidadesGanadas: number; oportunidadesPerdidas: number; tareasPendientes: number; tareasVencidas: number; importeAbierto: string|number; importeGanado: string|number; moneda: string }
export type Stage = {id: string; pipelineId: string; name: string; position: number; outcome: string; version: number}
export type QuotationLine = { id: string; productId: string; productName: string; productSku: string; unitPrice: string|number; quantity: string|number; discountPercent: string|number; taxPercent: string|number; lineTotal: string|number }
export type Quotation = Row & { number: string; opportunityId: string; status: string; currency: string; validUntil: string|null; subtotal: string|number; discountTotal: string|number; taxTotal: string|number; total: string|number; internalNotes: string|null; version: number; items: QuotationLine[] }
export type Problem = { title?: string; detail?: string; errors?: {field: string; message: string}[]; message?: string }

let csrf: { name: string; token: string } | null = null
const base = (import.meta.env.VITE_API_URL || '').replace(/\/$/, '')
export class ApiError extends Error { constructor(message: string, public status: number){super(message);this.name='ApiError'} }

function url(path: string){return `${base}/api/v1${path}`}
async function ensureCsrf(force = false){
 if(csrf && !force)return csrf
 const response = await fetch(url('/auth/csrf'),{credentials:'include',headers:{Accept:'application/json'}})
 if(!response.ok)throw new ApiError('No se pudo preparar la protección de la sesión.',response.status)
 const data = await response.json() as {headerName:string;token:string}
 csrf={name:data.headerName,token:data.token};return csrf
}
async function perform<T>(path: string, method='GET', body?: unknown, asBlob=false, retry=true): Promise<T> {
 const headers: Record<string,string>={Accept:asBlob?'*/*':'application/json'}
 if(body !== undefined) headers['Content-Type']='application/json'
 if(!['GET','HEAD'].includes(method)){const token=await ensureCsrf();headers[token.name]=token.token}
 let response: Response
 try { response=await fetch(url(path),{method,credentials:'include',headers,body:body===undefined?undefined:JSON.stringify(body)}) }
 catch {throw new ApiError('No se pudo conectar con el servidor. Comprueba que Spring Boot esté iniciado.',0)}
 if(response.status===403 && retry && !['GET','HEAD'].includes(method)){
  // El token puede rotar al iniciar/cerrar sesión o tras un reinicio del backend.
  await ensureCsrf(true);return perform<T>(path,method,body,asBlob,false)
 }
 if(!response.ok){
  let data:Problem={};try{data=await response.json() as Problem}catch{/* respuesta no JSON */}
  const friendly:Record<number,string>={400:'Revisa los campos del formulario.',401:'La sesión expiró. Vuelve a iniciar sesión.',403:'No tienes permisos para realizar esta operación.',404:'No se encontró el registro.',409:'Otro cambio impide guardar. Actualiza los datos.',422:'La operación no cumple las reglas comerciales.',429:'Demasiados intentos. Intenta más tarde.'}
  const fromFields=data.errors?.map(x=>`${x.field}: ${x.message}`).join(' · ')
  throw new ApiError(fromFields || data.detail || friendly[response.status] || data.title || `Error del servidor (${response.status}).`,response.status)
 }
 if(response.status===204)return undefined as T
 if(asBlob)return await response.blob() as T
 const contentType=response.headers.get('content-type')||''
 if(!contentType.includes('json'))return undefined as T
 return await response.json() as T
}
export const api={
 get:<T>(path:string)=>perform<T>(path),
 post:<T>(path:string,body:unknown)=>perform<T>(path,'POST',body),
 put:<T>(path:string,body:unknown)=>perform<T>(path,'PUT',body),
 patch:<T>(path:string,body:unknown)=>perform<T>(path,'PATCH',body),
 delete:<T>(path:string)=>perform<T>(path,'DELETE'),
 file:<T extends Blob=Blob>(path:string)=>perform<T>(path,'GET',undefined,true),
 login:async(email:string,password:string)=>{const user=await perform<SessionUser>('/auth/login','POST',{email,password});await ensureCsrf(true);return user},
 me:()=>perform<SessionUser>('/auth/me'),
 logout:async()=>{try{await perform<void>('/auth/logout','POST')}finally{csrf=null}},
 list:<T=Row>(resource:string,page=0,size=20,search='')=>perform<Page<T>>(`/${resource}?page=${page}&size=${size}${search?`&search=${encodeURIComponent(search)}`:''}`),
 download:async(path:string,name:string)=>{const b=await perform<Blob>(path,'GET',undefined,true);const objectUrl=URL.createObjectURL(b);const link=document.createElement('a');link.href=objectUrl;link.download=name;document.body.appendChild(link);link.click();link.remove();setTimeout(()=>URL.revokeObjectURL(objectUrl),30000)}
}
