import { useState, useEffect } from 'react'
import type { ReactNode } from 'react'
import { api, type Row } from './api'
import { Icon } from './Icon'
import { readable, money, date, dateTime, type Field } from './models'

export type AlertFn=(text:string,kind?:'success'|'error')=>void
export function Btn({children,onClick,variant='secondary',type='button',disabled=false,icon,loading=false,className=''}:{children:ReactNode;onClick?:()=>void;variant?:'primary'|'secondary'|'ghost'|'danger';type?:'button'|'submit';disabled?:boolean;icon?:string;loading?:boolean;className?:string}){return <button type={type} className={`btn btn-${variant} ${className}`} onClick={onClick} disabled={disabled||loading}>{icon&&<Icon name={icon} size={16}/>}<span>{loading?'Procesando…':children}</span></button>}
export function Badge({value}:{value:unknown}){const raw=String(value??'');const positive=['GANADA','ACEPTADA','ACTIVO','COMPLETADA','true','CALIFICADO'];const negative=['PERDIDA','RECHAZADA','DESCARTADO','CANCELADA','false'];return <span className={`pill ${positive.includes(raw)?'pill-good':negative.includes(raw)?'pill-bad':'pill-neutral'}`}>{readable(value)}</span>}
export function Empty({title='Sin registros',body='No hay información para mostrar con los filtros actuales.',action}:{title?:string;body?:string;action?:ReactNode}){return <div className="empty"><span className="empty-symbol">✳</span><h3>{title}</h3><p>{body}</p>{action}</div>}
export function Loading(){return <div className="loading"><span className="spinner"/>Consultando registros…</div>}
export function ErrorNotice({message,retry}:{message:string;retry?:()=>void}){return <div className="error-notice" role="alert"><Icon name="warning" size={20}/><span>{message}</span>{retry&&<Btn variant="ghost" onClick={retry}>Reintentar</Btn>}</div>}
export function PageHeading({eyebrow,title,subtitle,actions}:{eyebrow?:string;title:string;subtitle?:string;actions?:ReactNode}){return <header className="page-heading"><div><p className="eyebrow">{eyebrow||'REGISTRO COMERCIAL'}</p><h1>{title}</h1>{subtitle&&<p className="page-subtitle">{subtitle}</p>}</div>{actions&&<div className="page-actions">{actions}</div>}</header>}
export function Modal({title,subtitle,onClose,children,wide=false}:{title:string;subtitle?:string;onClose:()=>void;children:ReactNode;wide?:boolean}){
 useEffect(()=>{const esc=(e:KeyboardEvent)=>{if(e.key==='Escape')onClose()};window.addEventListener('keydown',esc);return()=>window.removeEventListener('keydown',esc)},[onClose]);
 return <div className="modal-layer" onMouseDown={e=>{if(e.target===e.currentTarget)onClose()}}><section className={`modal ${wide?'modal-wide':''}`} role="dialog" aria-modal="true" aria-label={title}><header className="modal-head"><div><span className="eyebrow">BOROUGH & LEDGER</span><h2>{title}</h2>{subtitle&&<p>{subtitle}</p>}</div><button className="icon-button" aria-label="Cerrar" onClick={onClose}><Icon name="close"/></button></header><div className="modal-body">{children}</div></section></div>
}
export function Confirm({title,message,onConfirm,onClose,busy}:{title:string;message:string;onConfirm:()=>void;onClose:()=>void;busy?:boolean}){return <Modal title={title} onClose={onClose}><p>{message}</p><div className="form-actions"><Btn onClick={onClose}>Cancelar</Btn><Btn variant="danger" disabled={busy} onClick={onConfirm}>Confirmar</Btn></div></Modal>}
export function Pager({page,total,onPage}:{page:number;total:number;onPage:(n:number)=>void}){if(total<=1)return null;return <nav className="pager" aria-label="Paginación"><Btn variant="ghost" disabled={page<=0} onClick={()=>onPage(page-1)}>Anterior</Btn><span>Página <strong>{page+1}</strong> de <strong>{total}</strong></span><Btn variant="ghost" disabled={page>=total-1} onClick={()=>onPage(page+1)}>Siguiente</Btn></nav>}
const resourceNames:Record<string,string>={customerId:'customers',contactId:'contacts',opportunityId:'opportunities',pipelineId:'pipelines'}
const nameCache=new Map<string,Promise<string>>()
export function ReferenceName({resource,id}:{resource:string;id:string}){
 const [name,setName]=useState('Consultando…')
 useEffect(()=>{let active=true;if(!id){setName('—');return}const key=`${resource}:${id}`;if(!nameCache.has(key)){nameCache.set(key,api.get<Row>(`/${resource}/${encodeURIComponent(id)}`).then(r=>resource==='contacts'?`${r.firstName||''} ${r.lastName||''}`.trim():String(r.name||r.title||id)).catch(()=>id.slice(0,8)+'…'))}nameCache.get(key)!.then(text=>{if(active)setName(text)});return()=>{active=false}},[resource,id]);return <span title={id}>{name}</span>
}
export function formatCell(key:string,v:unknown,row?:Row){if(v==null||v==='')return '—';if(['status','priority','kind','role','active','enabled'].includes(key))return <Badge value={v}/>;if(['unitPrice','amount','total','importeAbierto','importeGanado'].includes(key))return money(v,String(row?.currency||'PEN'));if(['dueAt','completedAt','nextContactAt','createdAt'].includes(key))return dateTime(v);if(['expectedClose','validUntil','closedAt'].includes(key))return date(v);if(resourceNames[key])return <ReferenceName resource={resourceNames[key]} id={String(v)}/>;if(['ownerId','stageId'].includes(key))return <span title={String(v)} className="uuid">{String(v).slice(0,8)}…</span>;return String(v)}

export function useLookup(resource:string,enabled=true,query=''){
 const [rows,setRows]=useState<Row[]>([]);const [busy,setBusy]=useState(false)
 useEffect(()=>{if(!enabled)return;let active=true;setBusy(true);api.list<Row>(resource,0,50,query).then(p=>{if(active)setRows(p.content)}).catch(()=>{if(active)setRows([])}).finally(()=>{if(active)setBusy(false)});return()=>{active=false}},[resource,enabled,query]);return {rows,busy}
}
function optionLabel(resource:string,r:Row){if(resource==='users')return String(r.displayName||r.email||r.id);if(resource==='contacts')return `${r.firstName||''} ${r.lastName||''}`.trim();if(resource==='opportunities')return String(r.title||r.id);return String(r.name||r.title||r.id)}
export function Relation({resource,value,onChange,required,disabled}:{resource:string;value:unknown;onChange:(v:string)=>void;required?:boolean;disabled?:boolean}){
 const [search,setSearch]=useState('');const {rows,busy}=useLookup(resource,!disabled,search)
 return <div className="relation"><select value={String(value||'')} onChange={e=>onChange(e.target.value)} required={required} disabled={disabled}><option value="">{busy?'Cargando…':'Sin asignar / Selecciona'}</option>{Boolean(value) && !rows.some(r=>r.id===value)&&<option value={String(value)}>Registro actual</option>}{rows.map(r=><option key={r.id} value={r.id}>{optionLabel(resource,r)}</option>)}</select><input aria-label={`Buscar ${resource}`} placeholder="Buscar en opciones…" value={search} disabled={disabled} onChange={e=>setSearch(e.target.value)}/></div>
}
export function FieldInput({field,value,onChange,disabled=false,role}:{field:Field;value:unknown;onChange:(v:unknown)=>void;disabled?:boolean;role?:string}){
 const type=field.type||'text'
 if(field.relation==='users'&&role!=='ADMIN')return null
 const control = type==='relation' && field.relation?<Relation resource={field.relation} value={value} required={field.required} onChange={onChange} disabled={disabled}/>
  :type==='textarea'?<textarea rows={3} value={String(value??'')} onChange={e=>onChange(e.target.value)} disabled={disabled} required={field.required}/>
  :type==='select'?<select value={String(value??field.default??'')} onChange={e=>onChange(e.target.value)} required={field.required} disabled={disabled}><option value="">Selecciona una opción</option>{field.options?.map(o=><option key={o.value} value={o.value}>{o.label}</option>)}</select>
  :type==='boolean'?<label className="switch-row"><input type="checkbox" checked={Boolean(value??field.default??false)} onChange={e=>onChange(e.target.checked)} disabled={disabled}/><span>{Boolean(value??field.default)?'Sí':'No'}</span></label>
  :<input type={type==='money'?'number':type} min={type==='money'?'0':undefined} step={type==='money'?'0.01':type==='number'?'1':undefined} value={String(value??'')} onChange={e=>onChange(e.target.value)} disabled={disabled} required={field.required} placeholder={field.hint||''}/>;
 return <label className={`field ${type==='textarea'?'field-full':''}`}><span>{field.label}{field.required&&<b className="required"> *</b>}</span>{control}{field.hint&&<small>{field.hint}</small>}</label>
}
