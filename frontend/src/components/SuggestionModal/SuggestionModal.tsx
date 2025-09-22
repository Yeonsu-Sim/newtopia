import React, { useState } from 'react'
import {
  ModalOverlay,
  ModalBackground,
  ModalContent,
  ModalInner,
  ModalHeader,
  HeaderTitle,
  HeaderSubtitle,
  ModalForm,
  FormFields,
  SelectWrapper,
  Select,
  InputWrapper,
  Input,
  TextareaWrapper,
  Textarea,
  FileUploadWrapper,
  FileUploadButton,
  FileUploadText,
  HiddenFileInput,
  FileList,
  FileItem,
  FileItemText,
  FileItemRemove,
  SubmitButtonWrapper,
  SubmitButton,
  SubmitButtonInner,
  SubmitButtonText,
  ModalFrame,
  ModalBorder,
  CloseButton,
} from '@/components/SuggestionModal/SuggestionModal.styles'

interface SuggestionModalProps {
  isOpen: boolean
  onClose: () => void
}

interface SuggestionForm {
  type: string
  title: string
  content: string
  files: File[]
}

export const SuggestionModal: React.FC<SuggestionModalProps> = ({
  isOpen,
  onClose,
}) => {
  const [formData, setFormData] = useState<SuggestionForm>({
    type: '버그신고',
    title: '',
    content: '',
    files: [],
  })
  const [isLoading, setIsLoading] = useState(false)

  if (!isOpen) return null

  const handleInputChange = (field: keyof SuggestionForm, value: string) => {
    setFormData((prev) => ({ ...prev, [field]: value }))
  }

  const handleFileSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = Array.from(e.target.files || [])
    setFormData((prev) => ({
      ...prev,
      files: [...prev.files, ...files],
    }))
  }

  const removeFile = (index: number) => {
    setFormData((prev) => ({
      ...prev,
      files: prev.files.filter((_, i) => i !== index),
    }))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!formData.title.trim() || !formData.content.trim()) {
      alert('제목과 내용을 모두 입력해주세요.')
      return
    }

    setIsLoading(true)

    try {
      // TODO: API 호출 구현
      await new Promise((resolve) => setTimeout(resolve, 1000)) // 임시 지연

      alert('건의사항이 성공적으로 제출되었습니다.')

      // 폼 초기화
      setFormData({
        type: '버그신고',
        title: '',
        content: '',
        files: [],
      })

      onClose()
    } catch (error) {
      console.error('건의사항 제출 중 오류 발생:', error)
      alert('건의사항 제출 중 오류가 발생했습니다. 다시 시도해주세요.')
    } finally {
      setIsLoading(false)
    }
  }

  const handleOverlayClick = (e: React.MouseEvent) => {
    if (e.target === e.currentTarget) {
      onClose()
    }
  }

  return (
    <ModalOverlay>
      <ModalBackground onClick={handleOverlayClick} />

      <ModalContent>
        <ModalInner>
          {/* 헤더 */}
          <ModalHeader>
            <HeaderTitle>
              <p>건의사항</p>
            </HeaderTitle>
            <HeaderSubtitle>
              <p>개선사항, 버그등이 있다면 알려주세요</p>
            </HeaderSubtitle>
          </ModalHeader>

          {/* 폼 */}
          <ModalForm onSubmit={handleSubmit}>
            <FormFields>
              {/* 건의 유형 드롭다운 */}
              <SelectWrapper>
                <Select
                  value={formData.type}
                  onChange={(e) => handleInputChange('type', e.target.value)}
                  required
                >
                  <option value="버그신고">유형 : 버그신고</option>
                  <option value="기능개선">유형 : 기능개선</option>
                  <option value="기타">유형 : 기타</option>
                </Select>
              </SelectWrapper>

              {/* 제목 */}
              <InputWrapper>
                <Input
                  type="text"
                  value={formData.title}
                  onChange={(e) => handleInputChange('title', e.target.value)}
                  placeholder="제목을 입력해주세요"
                  required
                />
              </InputWrapper>

              {/* 내용 */}
              <TextareaWrapper>
                <Textarea
                  value={formData.content}
                  onChange={(e) => handleInputChange('content', e.target.value)}
                  placeholder="내용을 입력해주세요"
                  required
                  rows={8}
                />
              </TextareaWrapper>

              {/* 파일 첨부 */}
              <FileUploadWrapper>
                <FileUploadButton as="label">
                  <HiddenFileInput
                    type="file"
                    multiple
                    accept="image/*"
                    onChange={handleFileSelect}
                  />
                  <FileUploadText>
                    📎 이미지를 첨부해주세요 (선택사항)
                  </FileUploadText>
                </FileUploadButton>

                {formData.files.length > 0 && (
                  <FileList>
                    {formData.files.map((file, index) => (
                      <FileItem key={index}>
                        <FileItemText>{file.name}</FileItemText>
                        <FileItemRemove onClick={() => removeFile(index)}>
                          ✕
                        </FileItemRemove>
                      </FileItem>
                    ))}
                  </FileList>
                )}
              </FileUploadWrapper>

              {/* 제출 버튼 */}
              <SubmitButtonWrapper>
                <SubmitButton type="submit" disabled={isLoading}>
                  <SubmitButtonInner>
                    <SubmitButtonText>
                      <p>{isLoading ? '제출 중...' : '제출하기'}</p>
                    </SubmitButtonText>
                  </SubmitButtonInner>
                </SubmitButton>
              </SubmitButtonWrapper>
            </FormFields>
          </ModalForm>
        </ModalInner>

        <ModalFrame />
        <ModalBorder />
        <CloseButton onClick={onClose}>✕</CloseButton>
      </ModalContent>
    </ModalOverlay>
  )
}
