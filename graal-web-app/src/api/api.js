import axios from 'axios'

export var API = axios.create({
  baseURL: 'http://localhost:8080/api/v1'
})
