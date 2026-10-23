export type FieldType = 'text'|'email'|'tel'|'textarea'|'select'|'date'|'datetime-local'|'money'|'number'|'boolean'|'relation'
export type Field = { key:string; label:string; type?:FieldType; required?:boolean; options?:{value:string;label:string}[]; relation?:string; dependsOn?:string; hint?:string; default?:string|boolean; readonly?:boolean }
export type Module = {slug:string; title:string; singular:string; caption:string; icon:string; columns:string[]; fields:Field[]; search?:boolean; noArchive?:boolean }
export const opts=(items:string[])=>items.map(x=>({value:x,label:x.replaceAll('_',' ')}))
export const statuses:Record<string,string>={ACTIVO:'Activo',ACTIVA:'Activa',ARCHIVADO:'Archivado',NUEVO:'Nuevo',CONTACTADO:'Contactado',CALIFICADO:'Calificado',DESCARTADO:'Descartado',CONVERTIDO:'Convertido',ABIERTA:'Abierta',GANADA:'Ganada',PERDIDA:'Perdida',PENDIENTE:'Pendiente',EN_PROGRESO:'En progreso',COMPLETADA:'Completada',CANCELADA:'Cancelada',BAJA:'Baja',MEDIA:'Media',ALTA:'Alta',BORRADOR:'Borrador',ENVIADA:'Enviada',ACEPTADA:'Aceptada',RECHAZADA:'Rechazada',LLAMADA:'Llamada',REUNION:'Reunión',CORREO_REGISTRADO:'Correo registrado',SEGUIMIENTO:'Seguimiento',OTRA:'Otra',PRODUCTO:'Producto',SERVICIO:'Servicio',ADMIN:'Administrador',MANAGER:'Gerente',SALES:'Comercial',VIEWER:'Consulta',CREATED:'Creado',UPDATED:'Actualizado',ARCHIVED:'Archivado',LOGIN:'Inicio de sesión',LOGOUT:'Cierre de sesión',MOVED:'Cambio de etapa',RENAMED:'Renombrado',CONVERTED:'Convertido',PASSWORD_CHANGED:'Contraseña modificada',PASSWORD_RESET:'Contraseña restablecida',UPDATED_DETAILS:'Ficha actualizada',CUSTOMER:'Cliente',CONTACT:'Contacto',LEAD:'Prospecto',PIPELINE:'Embudo',STAGE:'Etapa',PRODUCT:'Producto',OPPORTUNITY:'Oportunidad',QUOTATION:'Cotización',ORGANIZATION:'Organización',USER:'Usuario',true:'Sí',false:'No'}
export const readable=(s:unknown)=> s==null||s===''?'—':statuses[String(s)]||String(s).replaceAll('_',' ')
export const money=(amount:unknown,currency='PEN')=>{const n=Number(amount||0);try{return new Intl.NumberFormat('es-PE',{style:'currency',currency,maximumFractionDigits:2}).format(n)}catch{return `${currency} ${n.toFixed(2)}`}}
export const date=(input:unknown)=>{if(!input)return '—';const d=new Date(String(input));return isNaN(d.valueOf())?String(input):new Intl.DateTimeFormat('es-PE',{day:'2-digit',month:'short',year:'numeric'}).format(d)}
export const dateTime=(input:unknown)=>{if(!input)return '—';const d=new Date(String(input));return isNaN(d.valueOf())?String(input):new Intl.DateTimeFormat('es-PE',{day:'2-digit',month:'short',hour:'2-digit',minute:'2-digit'}).format(d)}
export const sections:{label:string; items:string[]}[]=[
 {label:'GENERAL',items:['inicio','clientes','contactos','prospectos']},
 {label:'VENTAS',items:['oportunidades','embudos','cotizaciones','catalogo']},
 {label:'OPERACIONES',items:['actividades','tareas','reportes']},
 {label:'ADMINISTRACIÓN',items:['usuarios','auditoria','configuracion']}
]
export const modules:Record<string,Module>={
 clientes:{slug:'customers',title:'Clientes',singular:'cliente',caption:'Empresas y cuentas comerciales',icon:'building',search:true,columns:['name','industry','email','phone','status'],fields:[
  {key:'name',label:'Razón social',required:true},{key:'industry',label:'Sector'},{key:'email',label:'Correo',type:'email'},{key:'phone',label:'Teléfono',type:'tel'}]},
 contactos:{slug:'contacts',title:'Contactos',singular:'contacto',caption:'Personas asociadas a empresas',icon:'users',search:true,columns:['firstName','lastName','jobTitle','email','customerId'],fields:[
  {key:'firstName',label:'Nombres',required:true},{key:'lastName',label:'Apellidos',required:true},{key:'email',label:'Correo',type:'email'},{key:'phone',label:'Teléfono',type:'tel'},{key:'jobTitle',label:'Cargo'},
  {key:'customerId',label:'Empresa',type:'relation',relation:'customers'},{key:'ownerId',label:'Responsable',type:'relation',relation:'users'},{key:'notes',label:'Notas',type:'textarea'}]},
 prospectos:{slug:'leads',title:'Prospectos',singular:'prospecto',caption:'Seguimiento y calificación comercial',icon:'spark',search:true,columns:['name','company','status','expectedValue','nextContactAt'],fields:[
  {key:'name',label:'Nombre',required:true},{key:'company',label:'Empresa'},{key:'email',label:'Correo',type:'email'},{key:'phone',label:'Teléfono',type:'tel'},
  {key:'source',label:'Origen'},{key:'status',label:'Estado',type:'select',required:true,default:'NUEVO',options:opts(['NUEVO','CONTACTADO','CALIFICADO','DESCARTADO'])},
  {key:'expectedValue',label:'Valor estimado',type:'money'},{key:'nextContactAt',label:'Próximo contacto',type:'datetime-local'},
  {key:'ownerId',label:'Responsable',type:'relation',relation:'users'},{key:'notes',label:'Observaciones',type:'textarea'}]},
 embudos:{slug:'pipelines',title:'Embudos',singular:'embudo',caption:'Procesos y etapas comerciales',icon:'layers',search:true,columns:['name','description','active'],fields:[
  {key:'name',label:'Nombre del embudo',required:true},{key:'description',label:'Descripción',type:'textarea'},{key:'active',label:'Activo',type:'boolean',default:true}]},
 actividades:{slug:'activities',title:'Actividades',singular:'actividad',caption:'Llamadas, reuniones y seguimientos',icon:'calendar',search:true,columns:['title','kind','dueAt','completedAt','customerId'],fields:[
  {key:'title',label:'Asunto',required:true},{key:'kind',label:'Tipo',type:'select',required:true,default:'LLAMADA',options:opts(['LLAMADA','REUNION','CORREO_REGISTRADO','SEGUIMIENTO','OTRA'])},
  {key:'dueAt',label:'Programada para',type:'datetime-local'},{key:'completedAt',label:'Realizada el',type:'datetime-local'},
  {key:'customerId',label:'Empresa',type:'relation',relation:'customers'},{key:'contactId',label:'Contacto',type:'relation',relation:'contacts'},
  {key:'opportunityId',label:'Oportunidad',type:'relation',relation:'opportunities'},
  {key:'ownerId',label:'Responsable',type:'relation',relation:'users'},{key:'details',label:'Detalle',type:'textarea'}]},
 tareas:{slug:'tasks',title:'Tareas',singular:'tarea',caption:'Trabajo pendiente y prioridades',icon:'check',search:true,columns:['title','priority','status','dueAt','ownerId'],fields:[
  {key:'title',label:'Título',required:true},{key:'description',label:'Descripción',type:'textarea'},
  {key:'status',label:'Estado',type:'select',required:true,default:'PENDIENTE',options:opts(['PENDIENTE','EN_PROGRESO','COMPLETADA','CANCELADA'])},
  {key:'priority',label:'Prioridad',type:'select',required:true,default:'MEDIA',options:opts(['BAJA','MEDIA','ALTA'])},
  {key:'dueAt',label:'Fecha límite',type:'datetime-local'},{key:'completedAt',label:'Fecha de finalización',type:'datetime-local'},
  {key:'ownerId',label:'Responsable',type:'relation',relation:'users'},{key:'opportunityId',label:'Oportunidad',type:'relation',relation:'opportunities'}]},
 catalogo:{slug:'products',title:'Productos y servicios',singular:'producto o servicio',caption:'Catálogo para cotizaciones',icon:'package',search:true,columns:['sku','name','kind','unitPrice','currency','active'],fields:[
  {key:'sku',label:'Código',required:true},{key:'name',label:'Nombre',required:true},{key:'description',label:'Descripción',type:'textarea'},
  {key:'unitPrice',label:'Precio unitario',type:'money',required:true,default:'0.00'},{key:'currency',label:'Moneda',required:true,type:'select',default:'PEN',options:opts(['PEN','USD','EUR'])},
  {key:'kind',label:'Tipo',type:'select',required:true,default:'SERVICIO',options:opts(['PRODUCTO','SERVICIO'])},
  {key:'active',label:'Disponible',type:'boolean',default:true}]}
}
export const columnLabels:Record<string,string>={name:'Nombre',firstName:'Nombres',lastName:'Apellidos',industry:'Sector',email:'Correo',phone:'Teléfono',status:'Estado',jobTitle:'Cargo',customerId:'Empresa',company:'Empresa',expectedValue:'Valor estimado',nextContactAt:'Próximo contacto',description:'Descripción',active:'Activo',title:'Título',kind:'Tipo',dueAt:'Programación',completedAt:'Realizado',priority:'Prioridad',ownerId:'Responsable',sku:'Código',unitPrice:'Precio',currency:'Moneda',amount:'Importe',expectedClose:'Cierre previsto',stageId:'Etapa',pipelineId:'Embudo',total:'Total',validUntil:'Válida hasta',number:'Número',displayName:'Nombre',role:'Rol',enabled:'Habilitado'}
