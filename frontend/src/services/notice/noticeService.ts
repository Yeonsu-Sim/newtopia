const API_BASE_URL = '/api/v1'

export const getNoticeList = async () => {
  const res = await fetch(
    `${API_BASE_URL}/notices`,
    {
      credentials: 'include',
    },
  )
  if (!res.ok) throw new Error('공지사항 조회 실패')
  const data = await res.json()
  console.log(data.data)
  return data
}