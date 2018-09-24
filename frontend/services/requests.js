import axios from "axios"

const baseUrl = "http://localhost:3002"

export const directories = () =>
  axios
    .get(`${baseUrl}`)
    .then(({ data }) => data)
    .catch(error => console.log("error", error))

export const directory = id =>
  axios
    .get(`${baseUrl}/directory/${id}`)
    .then(({ data }) => data)
    .catch(error => console.log("error", error))

export const getDataInfo = url =>
  axios
    .post(`${baseUrl}/fetch-url`, { url })
    .then(({ data }) => {
      console.log("fetch url")
      console.log(data)
      return data.content
    })
    .catch(error => console.log("error", error))

export const addDirectory = dirData => {
  console.log(dirData)
  return axios
    .post(`${baseUrl}/directory`, dirData)
    .then(({ data }) => data.content)
    .catch(error => console.log("error", error))
}
