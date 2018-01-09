import axios from 'axios'

export const API = axios.create({
  baseURL: 'http://172.18.0.6:8080/api/v1'
})
