const API = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

export async function request(path, options = {}) {
  const token = localStorage.getItem('intellidesk_token')
  const headers = { ...(options.body instanceof FormData ? {} : { 'Content-Type': 'application/json' }), ...(options.headers || {}) }
  if (token) headers.Authorization = `Bearer ${token}`
  const response = await fetch(`${API}${path}`, { ...options, headers })
  const text = await response.text()
  let data = null
  try { data = text ? JSON.parse(text) : null } catch { data = text }
  if (!response.ok) throw new Error(data?.message || data?.error || `Request failed (${response.status})`)
  return data
}

export function saveSession(data) {
  localStorage.setItem('intellidesk_token', data.token)
  localStorage.setItem('intellidesk_user', JSON.stringify({ id: data.id, name: data.name, email: data.email, role: data.role }))
}
export function getUser() {
  try { return JSON.parse(localStorage.getItem('intellidesk_user') || 'null') } catch { return null }
}
export function logout() {
  localStorage.removeItem('intellidesk_token')
  localStorage.removeItem('intellidesk_user')
}
