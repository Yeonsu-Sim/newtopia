import { useState, useEffect } from 'react'

interface UseTypewriterOptions {
  text: string
  speed?: number
  startDelay?: number
}

export const useTypewriter = ({
  text,
  speed = 50,
  startDelay = 0,
}: UseTypewriterOptions) => {
  const [displayText, setDisplayText] = useState('')
  const [isComplete, setIsComplete] = useState(false)

  useEffect(() => {
    setDisplayText('')
    setIsComplete(false)

    const timeout = setTimeout(() => {
      let index = 0
      const timer = setInterval(() => {
        if (index < text.length) {
          setDisplayText(text.slice(0, index + 1))
          index++
        } else {
          setIsComplete(true)
          clearInterval(timer)
        }
      }, speed)

      return () => clearInterval(timer)
    }, startDelay)

    return () => clearTimeout(timeout)
  }, [text, speed, startDelay])

  return { displayText, isComplete }
}
