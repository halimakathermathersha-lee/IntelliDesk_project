import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { request, saveSession } from '../services/api'
import Notice from '../components/Notice'

export default function Auth({ register = false }) {
  const [form, setForm] = useState({ name: '', email: '', password: '', role: 'USER' })
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  const navigate = useNavigate()
  async function submit(e) {
    e.preventDefault(); setError(''); setBusy(true)
    try {

      const data = await request(register ? '/api/auth/register' : '/api/auth/login', 
        { method: 'POST', body: JSON.stringify(form) })
      saveSession(data); navigate('/')
    } catch (err) { setError(err.message) } finally { setBusy(false) }
  }
  return <div className="min-h-screen grid lg:grid-cols-2 bg-white">
    <div className="hidden lg:flex bg-slate-950 text-white p-14 flex-col justify-between">
      <div className="text-2xl font-bold">IntelliDesk</div>
      <div>
        <div className="text-4xl font-semibold leading-tight max-w-lg">A clearer path from employee issue to resolution.</div>
      <p className="text-slate-300 mt-5 max-w-md">One workspace for reporting, routing and resolving internal support requests.</p></div>
      
      <div className="text-slate-400 text-sm">Internal issue management</div></div>
    
    
    <div className="flex items-center justify-center p-6">
      <form onSubmit={submit} className="w-full max-w-md">
        <div className="text-2xl font-bold mb-2">{register ? 'Create your account' : 'Welcome back'}</div>
        <p className="text-slate-500 mb-7">{register ? 'Join your company support workspace.' : 'Sign in to continue to IntelliDesk.'}</p>
        <Notice error={error} />
      {register && <label className="block text-sm font-medium mb-4">Full name<input required className="mt-1 w-full border rounded-lg p-3" value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} /></label>}

      <label className="block text-sm font-medium mb-4">Email address<input required type="email" className="mt-1 w-full border rounded-lg p-3" value={form.email} onChange={e => setForm({ ...form, email: e.target.value })} /></label>
      <label className="block text-sm font-medium mb-4">Password<input required minLength="8" type="password" className="mt-1 w-full border rounded-lg p-3" value={form.password} onChange={e => setForm({ ...form, password: e.target.value })} /></label>



      {register && <label className="block text-sm font-medium mb-5">Account role
        
        
        <select className="mt-1 w-full border rounded-lg p-3 bg-white" value={form.role} onChange={e => setForm({ ...form, role: e.target.value })}>
          <option value="USER">Employee</option>
          <option value="WORKER">Worker/Agent</option></select>
        <span className="text-xs text-slate-500">Admin accounts must be created by an administrator in a controlled environment.</span>
        
        </label>}

        {register && form.role === 'WORKER' && (
  <div className="mb-5">
    <label className="block text-sm font-medium">
      Your Specialization

      <select
        required
        className="mt-1 w-full border rounded-lg p-3 bg-white"
        value={form.specialization}
        onChange={(e) =>
          setForm({
            ...form,
            specialization: e.target.value
          })
        }
      >
        <option value="">Select specialization</option>
        <option value="Network">Network</option>
        <option value="Software">Software</option>
        <option value="Hardware">Hardware</option>
        <option value="General Support">General Support</option>
        <option value="Other">Other</option>
      </select>
    </label>

    {form.specialization === 'Other' && (
      <label className="block text-sm font-medium mt-4">
        Enter your specialization

        <input
          required
          className="mt-1 w-full border rounded-lg p-3"
          placeholder="e.g. Cybersecurity"
          value={form.customSpecialization}
          onChange={(e) =>
            setForm({
              ...form,
              customSpecialization: e.target.value
            })
          }
        />
      </label>
    )}
  </div>
)}


      <button disabled={busy} className="w-full rounded-lg bg-brand text-white py-3 font-semibold disabled:opacity-60">{busy ? 'Please wait…' : register ? 'Create account' : 'Sign in'}</button>

      <p className="text-sm text-slate-500 mt-5">{register ? 'Already have an account?' : 'New to IntelliDesk?'} <Link className="text-brand font-semibold" to={register ? '/login' : '/register'}>{register ? 'Sign in' : 'Create an account'}</Link></p>
    </form></div>
  </div>
}
