import { useState,useEffect,useCallback } from 'react'
import { api, type Row, type Page, type Stage } from './api'
import { type Module, type Field, columnLabels } from './models'
import { Icon } from './Icon'
import { Btn, Badge, Loading, Empty, ErrorNotice, PageHeading, Modal, Confirm, Pager, FieldInput, formatCell, type AlertFn, Relation } from './ui'

type Props={config:Module;role:string;notice:AlertFn}
const initialValue=(f:Field)=>f.default!==undefined?f.default:f.type==='boolean'?false:''
const editable=(role:string)=>['ADMIN','MANAGER','SALES'].includes(role)
const admin=(role:string)=>['ADMIN','MANAGER'].includes(role)
const cleanInput=(fields:Field[],values:Record<string,unknown>,role:string)=>Object.fromEntries(fields.filter(f=>!(f.relation==='users'&&role!=='ADMIN')).map(f=>{
 let v=values[f.key];if(f.type==='boolean')v=Boolean(v);else if(f.type==='select'||f.type==='relation'||f.type==='date'||f.type==='datetime-local'||f.type==='money'){v=v===''||v===undefined?null:v}else v=v===undefined?'':v
 if(f.type==='money' && v===null && f.required)v='0.00';return [f.key,v]
}))

export function CollectionPage({config,role,notice}:Props){
 const [rows,setRows]=useState<Page<Row>|null>(null),[search,setSearch]=useState(''),[page,setPage]=useState(0),[refresh,setRefresh]=useState(0)
 const [loading,setLoading]=useState(true),[error,setError]=useState(''),[editor,setEditor]=useState<Row|null|undefined>(undefined),[confirm,setConfirm]=useState<Row|null>(null),[busy,setBusy]=useState(false),[details,setDetails]=useState<Row|null>(null),[convert,setConvert]=useState<Row|null>(null),[stagesFor,setStagesFor]=useState<Row|null>(null)
 const reload=()=>setRefresh(n=>n+1)
 useEffect(()=>{setPage(0);setSearch('');setRows(null)},[config.slug])
 useEffect(()=>{let alive=true;const id=setTimeout(()=>{setLoading(true);setError('');api.list<Row>(config.slug,page,12,config.search?search:'').then(data=>{if(alive)setRows(data)}).catch(e=>{if(alive)setError((e as Error).message)}).finally(()=>{if(alive)setLoading(false)})},search?240:0);return()=>{alive=false;clearTimeout(id)}},[config.slug,config.search,page,search,refresh])
 const submit=async(values:Record<string,unknown>)=>{setBusy(true);try{
  const input=cleanInput(config.fields,values,role)
  if(editor?.id)await api.put(`/${config.slug}/${editor.id}`,input)
  else await api.post(`/${config.slug}`,input)
  notice(editor?.id?'Cambios guardados.':'Registro creado.');setEditor(undefined);reload()
 }catch(e){notice((e as Error).message,'error')}finally{setBusy(false)}}
 const archive=async()=>{if(!confirm)return;setBusy(true);try{await api.delete(`/${config.slug}/${confirm.id}`);notice('Registro archivado.');setConfirm(null);reload()}catch(e){notice((e as Error).message,'error')}finally{setBusy(false)}}
 const columns=config.columns
 return <>
  <PageHeading eyebrow="GESTIÓN COMERCIAL" title={config.title} subtitle={config.caption} actions={<>{config.slug==='pipelines'&&<span className="small-info">Etapas configurables</span>}{editable(role)&&<Btn icon="plus" variant="primary" onClick={()=>setEditor(null)}>Nuevo {config.singular}</Btn>}</>}/>
  <div className="panel table-panel"><div className="table-toolbar"><div className="record-count"><strong>{rows?.totalElements??'—'}</strong><span> registros</span></div><div className="toolbar-actions">{config.search&&<label className="searchbox"><Icon name="search" size={17}/><input aria-label={`Buscar ${config.title}`} placeholder="Buscar registros…" value={search} onChange={e=>{setPage(0);setSearch(e.target.value)}}/></label>}<button className="icon-button" title="Actualizar" aria-label="Actualizar lista" onClick={reload}><Icon name="refresh"/></button></div></div>
  {error?<ErrorNotice message={error} retry={reload}/>:loading&&!rows?<Loading/>:rows?.content.length===0?<Empty title="No hay registros" body={search?'No se encontraron resultados. Ajusta la búsqueda.':'Crea el primer registro para comenzar.'} action={editable(role)&&<Btn variant="primary" icon="plus" onClick={()=>setEditor(null)}>Crear {config.singular}</Btn>}/>:
  <div className="table-scroll"><table className="data-table"><thead><tr>{columns.map(c=><th key={c}>{columnLabels[c]||c}</th>)}<th className="row-actions">Acciones</th></tr></thead><tbody>{rows?.content.map(row=><tr key={row.id}>{columns.map((key,ix)=><td key={key} className={ix===0?'leading-cell':''}>{key==='status'||key==='active'?<Badge value={row[key]}/>:formatCell(key,row[key],row)}</td>)}<td><div className="action-group"><button title="Abrir ficha" className="tiny-action" onClick={()=>setEditor(row)}><Icon name="edit" size={16}/></button>{config.slug==='customers'&&<button title="Ficha ampliada" className="tiny-action" onClick={()=>setDetails(row)}><Icon name="file" size={16}/></button>}{config.slug==='leads'&&row.status==='CALIFICADO'&&editable(role)&&<button title="Convertir prospecto" className="tiny-action" onClick={()=>setConvert(row)}><Icon name="arrow" size={16}/></button>}{config.slug==='pipelines'&&<button title="Administrar etapas" className="tiny-action" onClick={()=>setStagesFor(row)}><Icon name="layers" size={16}/></button>}{admin(role)&&!config.noArchive&&<button title="Archivar" className="tiny-action danger-icon" onClick={()=>setConfirm(row)}><Icon name="trash" size={16}/></button>}</div></td></tr>)}</tbody></table></div>}
  <div className="table-footer"><p>Mostrando {rows?.content.length||0} de {rows?.totalElements||0} registros</p><Pager page={page} total={rows?.totalPages||0} onPage={setPage}/></div></div>
  {editor!==undefined&&<RecordEditor title={editor?.id?`Ficha de ${config.singular}`:`Nuevo ${config.singular}`} fields={config.fields} initial={editor} role={role} busy={busy} canWrite={editable(role)} onClose={()=>setEditor(undefined)} onSave={submit}/>}
  {confirm&&<Confirm title={`Archivar ${config.singular}`} message="Este registro dejará de aparecer en las consultas activas. La operación quedará registrada en auditoría." onClose={()=>setConfirm(null)} onConfirm={archive} busy={busy}/>}
  {details&&<CustomerDetails customer={details} role={role} notice={notice} onClose={()=>setDetails(null)}/>}
  {convert&&<ConvertLead lead={convert} notice={notice} onClose={()=>setConvert(null)} onConverted={()=>{setConvert(null);reload()}}/>}
  {stagesFor&&<StageManager pipeline={stagesFor} role={role} notice={notice} onClose={()=>setStagesFor(null)}/>}
 </>
}

export function RecordEditor({title,fields,initial,role,busy,canWrite,onClose,onSave}:{title:string;fields:Field[];initial?:Row|null;role:string;busy:boolean;canWrite:boolean;onClose:()=>void;onSave:(v:Record<string,unknown>)=>Promise<void>}){
 const [values,setValues]=useState<Record<string,unknown>>(()=>Object.fromEntries(fields.map(f=>[f.key, initial?.[f.key]??initialValue(f)])))
 return <Modal title={title} subtitle={initial?.id?`Identificador: ${initial.id}`:undefined} onClose={onClose} wide>
 <form onSubmit={e=>{e.preventDefault();void onSave(values)}}><div className="form-grid">{fields.map(f=><FieldInput key={f.key} role={role} field={f} value={values[f.key]} onChange={v=>setValues(o=>({...o,[f.key]:v}))} disabled={!canWrite||busy}/>)}</div><div className="form-actions"><Btn onClick={onClose}>Cerrar</Btn>{canWrite&&<Btn variant="primary" type="submit" loading={busy} icon="save">{initial?.id?'Guardar cambios':'Crear registro'}</Btn>}</div></form>
 </Modal>
}

function CustomerDetails({customer,role,notice,onClose}:{customer:Row;role:string;notice:AlertFn;onClose:()=>void}){
 const id=customer.id;const [data,setData]=useState<Record<string,unknown>>({}),[busy,setBusy]=useState(false),[error,setError]=useState('')
 const fields:Field[]=[{key:'commercialName',label:'Nombre comercial'},{key:'taxId',label:'Identificación fiscal / RUC'},{key:'country',label:'País'},{key:'address',label:'Dirección'},{key:'website',label:'Sitio web'},{key:'ownerId',label:'Responsable',relation:'users',type:'relation'},{key:'description',label:'Descripción',type:'textarea'}]
 useEffect(()=>{api.get<Record<string,unknown>>(`/customers/${id}/details`).then(setData).catch(e=>setError((e as Error).message))},[id])
 const save=async()=>{setBusy(true);try{await api.patch(`/customers/${id}/details`,Object.fromEntries(fields.map(f=>[f.key,data[f.key]||null])));notice('Ficha del cliente actualizada.');onClose()}catch(e){setError((e as Error).message)}finally{setBusy(false)}}
 return <Modal title={`Ficha ampliada · ${customer.name}`} onClose={onClose} wide>{error&&<ErrorNotice message={error}/>}<form onSubmit={e=>{e.preventDefault();void save()}}><div className="form-grid">{fields.map(f=><FieldInput key={f.key} role={role} field={f} value={data[f.key]} onChange={v=>setData(o=>({...o,[f.key]:v}))} disabled={!editable(role)||busy}/>)}</div><div className="form-actions"><Btn onClick={onClose}>Cerrar</Btn>{editable(role)&&<Btn variant="primary" type="submit" loading={busy}>Guardar ficha</Btn>}</div></form></Modal>
}

function ConvertLead({lead,onClose,notice,onConverted}:{lead:Row;onClose:()=>void;notice:AlertFn;onConverted:()=>void}){
 const [pipelines,setPipelines]=useState<Row[]>([]),[pipelineId,setPipelineId]=useState(''),[stageId,setStageId]=useState(''),[stages,setStages]=useState<Stage[]>([]),[customerId,setCustomerId]=useState(''),[contactId,setContactId]=useState(''),[title,setTitle]=useState(`Oportunidad de ${lead.name}`),[currency,setCurrency]=useState('PEN'),[busy,setBusy]=useState(false),[error,setError]=useState('')
 useEffect(()=>{api.list<Row>('pipelines',0,50).then(r=>setPipelines(r.content)).catch(e=>setError((e as Error).message))},[])
 useEffect(()=>{setStageId('');if(!pipelineId)return;api.get<Stage[]>(`/pipelines/${pipelineId}/stages`).then(r=>setStages(r.filter(s=>s.outcome==='ABIERTA'))).catch(e=>setError((e as Error).message))},[pipelineId])
 const convert=async()=>{setBusy(true);setError('');try{await api.post(`/leads/${lead.id}/convert`,{pipelineId,stageId,customerId:customerId||null,contactId:contactId||null,dealTitle:title,currency});notice('Prospecto convertido en oportunidad.');onConverted()}catch(e){setError((e as Error).message)}finally{setBusy(false)}}
 return <Modal title={`Convertir prospecto · ${lead.name}`} subtitle="El sistema creará la oportunidad y vinculará los registros necesarios." onClose={onClose} wide><form onSubmit={e=>{e.preventDefault();void convert()}}><div className="form-grid"><label className="field"><span>Embudo *</span><select required value={pipelineId} onChange={e=>setPipelineId(e.target.value)}><option value="">Selecciona un embudo</option>{pipelines.map(p=><option key={p.id} value={p.id}>{String(p.name)}</option>)}</select></label><label className="field"><span>Etapa inicial *</span><select required value={stageId} onChange={e=>setStageId(e.target.value)}><option value="">Selecciona una etapa abierta</option>{stages.map(s=><option key={s.id} value={s.id}>{s.name}</option>)}</select></label><label className="field"><span>Nombre de oportunidad</span><input required value={title} onChange={e=>setTitle(e.target.value)}/></label><label className="field"><span>Moneda</span><select value={currency} onChange={e=>setCurrency(e.target.value)}><option>PEN</option><option>USD</option><option>EUR</option></select></label><label className="field"><span>Cliente existente (opcional)</span><Relation resource="customers" value={customerId} onChange={setCustomerId}/></label><label className="field"><span>Contacto existente (opcional)</span><Relation resource="contacts" value={contactId} onChange={setContactId}/></label></div>{error&&<ErrorNotice message={error}/>}<div className="form-actions"><Btn onClick={onClose}>Cancelar</Btn><Btn type="submit" variant="primary" loading={busy}>Convertir prospecto</Btn></div></form></Modal>
}

function StageManager({pipeline,role,notice,onClose}:{pipeline:Row;role:string;notice:AlertFn;onClose:()=>void}){
 const [stages,setStages]=useState<Stage[]>([]),[loading,setLoading]=useState(true),[editor,setEditor]=useState<Stage|null|undefined>(undefined),[busy,setBusy]=useState(false),[error,setError]=useState('')
 const load=useCallback(()=>{setLoading(true);api.get<Stage[]>(`/pipelines/${pipeline.id}/stages`).then(setStages).catch(e=>setError((e as Error).message)).finally(()=>setLoading(false))},[pipeline.id]);useEffect(()=>load(),[load])
 const [form,setForm]=useState({name:'',position:0,outcome:'ABIERTA'})
 const open=(stage?:Stage)=>{setEditor(stage||null);setForm({name:stage?.name||'',position:stage?.position??stages.length,outcome:stage?.outcome||'ABIERTA'})}
 const save=async()=>{setBusy(true);try{if(editor?.id)await api.put(`/pipelines/${pipeline.id}/stages/${editor.id}`,form);else await api.post(`/pipelines/${pipeline.id}/stages`,form);notice('Etapa guardada.');setEditor(undefined);load()}catch(e){setError((e as Error).message)}finally{setBusy(false)}}
 return <Modal title={`Etapas · ${pipeline.name}`} onClose={onClose} wide><div className="section-toolbar"><span className="eyebrow">SECUENCIA DEL EMBUDO</span>{admin(role)&&<Btn variant="primary" icon="plus" onClick={()=>open()}>Agregar etapa</Btn>}</div>{loading?<Loading/>:<div className="stage-list">{stages.map(s=><div key={s.id} className="stage-item"><span className="stage-index">{String(s.position+1).padStart(2,'0')}</span><span className="stage-label">{s.name}</span><Badge value={s.outcome}/>{admin(role)&&<button className="tiny-action" aria-label={`Editar ${s.name}`} onClick={()=>open(s)}><Icon name="edit"/></button>}</div>)}</div>}{error&&<ErrorNotice message={error}/>}<div className="form-actions"><Btn onClick={onClose}>Cerrar</Btn></div>
 {editor!==undefined&&<Modal title={editor?.id?'Editar etapa':'Nueva etapa'} onClose={()=>setEditor(undefined)}><form onSubmit={e=>{e.preventDefault();void save()}}><div className="form-grid"><label className="field"><span>Nombre *</span><input required value={form.name} onChange={e=>setForm(o=>({...o,name:e.target.value}))}/></label><label className="field"><span>Posición</span><input type="number" min={0} value={form.position} onChange={e=>setForm(o=>({...o,position:Number(e.target.value)}))}/></label><label className="field"><span>Resultado</span><select value={form.outcome} onChange={e=>setForm(o=>({...o,outcome:e.target.value}))}><option value="ABIERTA">Abierta</option><option value="GANADA">Ganada</option><option value="PERDIDA">Perdida</option></select></label></div>{error&&<ErrorNotice message={error}/>}<div className="form-actions"><Btn onClick={()=>setEditor(undefined)}>Cancelar</Btn><Btn type="submit" variant="primary" loading={busy}>Guardar etapa</Btn></div></form></Modal>}
 </Modal>
}
