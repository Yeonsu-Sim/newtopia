import { useEffect, useState } from 'react'
import {
  getReportContext,
  getReportGraph,
  getGameResultTurnDetail,
} from '@/services/report/reportService'

export const useReportContext = (gameId: number) => {
  const [data, setData] = useState<any>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<Error | null>(null)

  useEffect(() => {
    setLoading(true)
    getReportContext(gameId)
      .then(setData)
      .catch(setError)
      .finally(() => setLoading(false))
  }, [gameId])

  return { data, loading, error }
}

export const useReportGraph = (gameId: number, size: number = 200) => {
  const [data, setData] = useState<any>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<Error | null>(null)

  useEffect(() => {
    setLoading(true)
    getReportGraph(gameId, size)
      .then(setData)
      .catch(setError)
      .finally(() => setLoading(false))
  }, [gameId, size])

  return { data, loading, error }
}

export const useGameResultTurnDetail = (gameId: number, turnNumber: number) => {
  const [data, setData] = useState<any>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<Error | null>(null)

  useEffect(() => {
    if (!turnNumber) return
    setLoading(true)
    getGameResultTurnDetail(gameId, turnNumber)
      .then(setData)
      .catch(setError)
      .finally(() => setLoading(false))
  }, [gameId, turnNumber])

  return { data, loading, error }
}
