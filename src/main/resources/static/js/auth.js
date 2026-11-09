const loginTab=document.getElementById('loginTab');
const registerTab=document.getElementById('registerTab');
const loginForm=document.getElementById('loginForm');
const registerForm=document.getElementById('registerForm');
const authMessage=document.getElementById('authMessage');

function showTab(register){
  loginForm.classList.toggle('hidden',register);
  registerForm.classList.toggle('hidden',!register);
  loginTab.classList.toggle('active',!register);
  registerTab.classList.toggle('active',register);
  authMessage.textContent='';
}
loginTab.addEventListener('click',()=>showTab(false));
registerTab.addEventListener('click',()=>showTab(true));

loginForm.addEventListener('submit',async e=>{
  e.preventDefault(); authMessage.className='message'; authMessage.textContent='Signing in...';
  try{
    const res=await fetch('/api/auth/login',{method:'POST',headers:{'Content-Type':'application/json'},
      body:JSON.stringify({email:loginEmail.value,password:loginPassword.value})});
    const data=await res.json();
    if(!res.ok) throw new Error(data.message||data.detail||'Login failed.');
    location.href=data.role==='ADMIN'?'/admin.html':'/student.html';
  }catch(err){authMessage.textContent=err.message;}
});
registerForm.addEventListener('submit',async e=>{
  e.preventDefault(); authMessage.className='message'; authMessage.textContent='Creating account...';
  try{
    const res=await fetch('/api/auth/register',{method:'POST',headers:{'Content-Type':'application/json'},
      body:JSON.stringify({fullName:regName.value,email:regEmail.value,password:regPassword.value})});
    const data=await res.json();
    if(!res.ok) throw new Error(data.message||data.detail||'Registration failed.');
    authMessage.className='message success'; authMessage.textContent='Account created. You can now log in.';
    registerForm.reset(); showTab(false);
    authMessage.className='message success'; authMessage.textContent='Account created. Please log in.';
  }catch(err){authMessage.textContent=err.message;}
});
