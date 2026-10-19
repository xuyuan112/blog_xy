import { adminPost } from '@/utils/request'

export interface FileUploadResult {
  url: string
  thumbnailUrl?: string
  fileName: string
  size: number
}

export function uploadFile(file: File, scene: string) {
  const data = new FormData()
  data.append('file', file)
  data.append('scene', scene)
  return adminPost<FileUploadResult>('/files/upload', data)
}

export function uploadImage(file: File, scene = 'article') {
  const data = new FormData()
  data.append('file', file)
  data.append('scene', scene)
  return adminPost<FileUploadResult>('/files/upload/image', data)
}
