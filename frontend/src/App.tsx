import { useEffect, useState } from 'react'

type Health = { status: string }

export default function App() {
  const [status, setStatus] = useState('loading...')

  useEffect(() => {
    fetch('/actuator/health')
      .then((res) => res.json() as Promise<Health>)
      .then((data) => setStatus(data.status))
      .catch(() => setStatus('unreachable'))
  }, [])

  return <h1>Backend: {status}</h1>
}
