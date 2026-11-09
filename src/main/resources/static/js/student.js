let complaints=[];
const $=id=>document.getElementById(id);
async function api(url,options={}){
  const res=await fetch(url,{...options,headers:{'Content-Type':'application/json',...(options.headers||{})}});
  const data=await res.json().catch(()=>({}));
  if(!res.ok) throw new Error(data.message||data.detail||'Request failed. Please try again.');
  return data;
}
function safe(value){return String(value??'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));}
function statusClass(s){return s.toLowerCase().replaceAll(' ','-');}
function render(){
  $('totalCount').textContent=complaints.length;
  $('pendingCount').textContent=complaints.filter(c=>c.status==='Pending').length;
  $('resolvedCount').textContent=complaints.filter(c=>c.status==='Resolved').length;
  const term=$('searchInput').value.toLowerCase(), status=$('statusFilter').value;
  const filtered=complaints.filter(c=>(!status||c.status===status)&&
    [c.title,c.description,c.category].some(v=>String(v).toLowerCase().includes(term)));
  $('complaintList').innerHTML=filtered.length?filtered.map(c=>`
    <article class="complaint-card">
      <div class="complaint-top"><div><h3>${safe(c.title)}</h3><div class="meta"><span>${safe(c.category)}</span><span>•</span><span>${new Date(c.createdAt).toLocaleString()}</span></div></div>
      <span class="pill status-${statusClass(c.status)}">${safe(c.status)}</span></div>
      <p>${safe(c.description)}</p>
      <div class="meta"><span class="pill priority-${c.priority.toLowerCase()}">${safe(c.priority)} priority</span><span>Complaint #${c.id}</span></div>
    </article>`).join(''):'<div class="empty">No complaints found. Submit your first complaint using the form.</div>';
}
async function load(){
  try{complaints=await api('/api/complaints/mine');render();}
  catch(e){if(e.message.toLowerCase().includes('log in'))location.href='/';else $('complaintList').innerHTML=`<div class="empty">${safe(e.message)}</div>`;}
}
(async()=>{
  try{const user=await api('/api/auth/me');if(user.role!=='STUDENT'){location.href='/admin.html';return;}
    $('welcomeName').textContent='Hi, '+user.fullName;$('todayDate').textContent=new Date().toLocaleDateString(undefined,{day:'numeric',month:'short',year:'numeric'});
    await load();
  }catch(e){location.href='/';}
})();
$('complaintForm').addEventListener('submit',async e=>{
  e.preventDefault();$('formMessage').className='message';$('formMessage').textContent='Submitting...';
  try{
    await api('/api/complaints',{method:'POST',body:JSON.stringify({title:$('title').value,category:$('category').value,
      priority:$('priority').value,description:$('description').value})});
    $('complaintForm').reset();$('priority').value='Medium';
    $('formMessage').className='message success';$('formMessage').textContent='Complaint submitted successfully.';
    await load();
  }catch(err){$('formMessage').textContent=err.message;}
});
$('searchInput').addEventListener('input',render);$('statusFilter').addEventListener('change',render);
$('refreshBtn').addEventListener('click',load);
$('logoutBtn').addEventListener('click',async()=>{await fetch('/api/auth/logout',{method:'POST'});location.href='/';});
