let complaints=[];
const $=id=>document.getElementById(id);
async function api(url,options={}){
  const res=await fetch(url,{...options,headers:{'Content-Type':'application/json',...(options.headers||{})}});
  const data=await res.json().catch(()=>({}));
  if(!res.ok) throw new Error(data.message||data.detail||'Request failed.');
  return data;
}
function safe(value){return String(value??'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));}
function statusClass(s){return s.toLowerCase().replaceAll(' ','-');}
function render(){
  $('totalCount').textContent=complaints.length;
  $('pendingCount').textContent=complaints.filter(c=>c.status==='Pending').length;
  $('progressCount').textContent=complaints.filter(c=>c.status==='In Progress').length;
  $('resolvedCount').textContent=complaints.filter(c=>c.status==='Resolved').length;
  const term=$('searchInput').value.toLowerCase(),status=$('statusFilter').value,priority=$('priorityFilter').value;
  const rows=complaints.filter(c=>(!status||c.status===status)&&(!priority||c.priority===priority)&&
    [c.title,c.description,c.studentName,c.category].some(v=>String(v).toLowerCase().includes(term)));
  $('adminTable').innerHTML=rows.length?rows.map(c=>`
    <tr><td><strong>${safe(c.title)}</strong><div class="table-description">${safe(c.description)}</div><small>#${c.id}</small></td>
    <td>${safe(c.studentName)}</td><td>${safe(c.category)}</td>
    <td><span class="pill priority-${c.priority.toLowerCase()}">${safe(c.priority)}</span></td>
    <td>${new Date(c.createdAt).toLocaleDateString()}</td>
    <td><select class="status-select" data-id="${c.id}" aria-label="Update status for complaint ${c.id}">
      ${['Pending','In Progress','Resolved'].map(s=>`<option ${s===c.status?'selected':''}>${s}</option>`).join('')}
    </select></td></tr>`).join(''):'<tr><td colspan="6" class="empty">No complaints match your filters.</td></tr>';
}
async function load(){
  try{complaints=await api('/api/admin/complaints');render();}
  catch(e){$('adminMessage').textContent=e.message;if(e.message.toLowerCase().includes('log in')||e.message.toLowerCase().includes('administrator'))location.href='/';}
}
(async()=>{
  try{const user=await api('/api/auth/me');if(user.role!=='ADMIN'){location.href='/student.html';return;}await load();}
  catch(e){location.href='/';}
})();
$('adminTable').addEventListener('change',async e=>{
  if(!e.target.matches('.status-select'))return;
  const id=e.target.dataset.id;
  try{
    await api(`/api/admin/complaints/${id}/status`,{method:'PATCH',body:JSON.stringify({status:e.target.value})});
    $('adminMessage').className='message success';$('adminMessage').textContent='Complaint status updated.';
    await load();
  }catch(err){$('adminMessage').className='message';$('adminMessage').textContent=err.message;await load();}
});
['searchInput','statusFilter','priorityFilter'].forEach(id=>$(id).addEventListener(id==='searchInput'?'input':'change',render));
$('refreshBtn').addEventListener('click',load);
$('logoutBtn').addEventListener('click',async()=>{await fetch('/api/auth/logout',{method:'POST'});location.href='/';});
