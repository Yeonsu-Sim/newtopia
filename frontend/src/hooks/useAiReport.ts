import { useEffect, useState } from 'react'

export interface SectionBlock {
  type: string
  title: string
  bullets?: string[]
  text?: string
}

export interface ReportData {
  status: 'PENDING' | 'PROCESSING' | 'READY' | 'ERROR'
  promptHash: string | null
  sections: {
    blocks: {
      highlights?: SectionBlock
      ending?: SectionBlock
      brief?: SectionBlock
    }
  } | null
  subscribeUrl: string | null
}

type ServerEvent = {
  data: ReportData
  status: 'success' | 'error'
  message?: string
  error?: string | null
}

type AiReportHandlers = {
  onPending?: (data: ReportData) => void
  onProcessing?: (data: ReportData) => void
  onReady?: (data: ReportData) => void
  onError?: (data: ReportData | null, err?: unknown) => void
  onHeartbeat?: () => void
  onOpen?: () => void
  onClose?: () => void
}

function parse(e: MessageEvent): ServerEvent | null {
  try {
    return JSON.parse(e.data)
  } catch {
    return null
  }
}

export const useAiReport = (gameId: number, handlers?: AiReportHandlers) => {
  const [report, setReport] = useState<ReportData | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!gameId) return
    let isClosed = false
    const url = `/api/v1/game-results/${gameId}/report/summary/stream`
    const es = new EventSource(url, { withCredentials: true })

    es.onopen = () => {
      handlers?.onOpen?.()
      console.log('[SSE] 연결 열림')
    }

    es.addEventListener('heartbeat', () => {
      handlers?.onHeartbeat?.()
      console.log('[SSE] heartbeat: 연결 종료!')
      if (!isClosed) {
        es.close()
        handlers?.onClose?.()
        isClosed = true
      }
    })

    const handleEvent = (e: MessageEvent, callback?: (data: ReportData) => void, setLoadingState = true) => {
      const r = parse(e)
      if (r?.data) {
        setReport(r.data)
        setLoading(setLoadingState)
        callback?.(r.data)
        console.log(`[SSE] ${r.data.status.toLowerCase()}`, r.data)
      } else if (r?.status === 'error') {
        setError(r.message || '리포트 생성 중 오류 발생')
        setLoading(false)
        handlers?.onError?.(null, r.error)
        console.error('[SSE] error', r)
      }
    }

    es.addEventListener('pending', (e) => handleEvent(e, handlers?.onPending))
    es.addEventListener('processing', (e) => handleEvent(e, handlers?.onProcessing))
    es.addEventListener('ready', (e) => handleEvent(e, handlers?.onReady, false))
    es.addEventListener('error', (e: Event) => {
      handleEvent(e as MessageEvent, handlers?.onError, false)
    })


    return () => {
      if (!isClosed) es.close()
      console.log('[SSE] cleanup: 연결 닫음')
    }
  }, [gameId])

  return { report, loading, error }
}
