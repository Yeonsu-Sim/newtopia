import { useEffect, useState } from 'react'
import { getNoticeList } from '@/services/notice/noticeService'

export interface Notice {
  id: number
  title: string
  content: string
  imgUrl: string
}

export const useNotice = () => {
  const [notices, setNotices] = useState<Notice[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const fetchNotices = async () => {
      setLoading(true)
      setError(null)
      try {
        const data = await getNoticeList()
        setNotices(data.data || data)
      } catch (err: any) {
        setError(err.message)
      } finally {
        setLoading(false)
      }
    }

    fetchNotices()
  }, [])

  return { notices, loading, error }
}
