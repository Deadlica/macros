import { useEffect, useState } from 'react'

type Health = {
  groups?: string[]
  status: string
}

export default function App() {
  const [groups, setGroups] = useState<string[]>([])
  const [status, setStatus] = useState('loading...')

  useEffect(() => {
    fetch('/actuator/health')
      .then((res) => res.json() as Promise<Health>)
      .then((data) => {
        setGroups(data.groups ?? [])
        setStatus(data.status)
      })
      .catch(() => setStatus('unreachable'))
  }, [])

  return (
    <>
      <h1>Backend: {status}</h1>
      <p>Groups: {groups.join(',')}</p>
    </>
  )
}
