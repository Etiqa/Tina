import axios from "axios"
import Cookies from "js-cookie"

const baseUrl = "http://localhost:3002"

const axInstance = () =>
  axios.create({
    headers: { "auth-token": Cookies.get("auth-token") }
  })

const handleError = error => {
  const {
    response: { status }
  } = error
  console.log(status)
  if (status === 403 || status === 401) {
    window.location.href = "/"
  } else {
    console.log("error", error)
  }
}

export const directories = () =>
  axInstance()
    .get(`${baseUrl}`)
    .then(({ data }) => data)
    .catch(handleError)

export const directory = id =>
  axInstance()
    .get(`${baseUrl}/directory/${id}`)
    .then(({ data }) => data)
    .catch(handleError)

export const getDataInfo = url =>
  axInstance()
    .post(`${baseUrl}/fetch-url`, { url })
    .then(({ data }) => data.content)
    .catch(handleError)

export const addDirectory = dirData => {
  return axInstance()
    .post(`${baseUrl}/directory`, dirData)
    .then(({ data }) => data.content)
    .catch(handleError)
}

export const addService = (dirId, dirData) => {
  return axInstance()
    .post(`${baseUrl}/directory/${dirId}`, dirData)
    .then(({ data }) => data.content)
    .catch(handleError)
}

export const updateService = (dirId, serviceId, dirData) => {
  return axInstance()
    .put(`${baseUrl}/directory/${dirId}/${serviceId}`, dirData)
    .then(({ data }) => data.content)
    .catch(handleError)
}

export const deleteService = (dirId, serviceId) => {
  return axInstance()
    .delete(`${baseUrl}/directory/${dirId}/${serviceId}`)
    .then(({ data }) => data.content)
    .catch(handleError)
}

export const deleteDirectory = dirId => {
  return axInstance()
    .delete(`${baseUrl}/directory/${dirId}`)
    .then(({ data }) => data.content)
    .catch(handleError)
}

// USER API

export const signup = user => {
  return axInstance()
    .post(`${baseUrl}/signup`, user)
    .then(({ data }) => data)
    .catch(({ response: { data: { error } } }) => Promise.reject(error))
}

export const signin = user => {
  return axInstance()
    .post(`${baseUrl}/signin`, user)
    .then(({ data: { token } }) => {
      Cookies.set("auth-token", token, { path: "/" })
      return token
    })
    .catch(({ response: { data: { error } } }) => Promise.reject(error))
}
