<template>
  <div class="col-md-12">
    <h3>{{ name }}</h3>

    <div v-if="!edit">
      <div class="float-right">
        <button class="btn btn-primary" @click.prevent="toggleEdit"><i class="fas fa-pencil-alt" /></button>

        <button class="btn btn-danger"><i class="fas fa-ban"/></button>
      </div>

      <ul class="">
        <li class="">
          URL: <span>{{ internalUrl }}</span>
        </li>
        <li class="">
          Raw Response:
          <button class="btn btn-success" @click.prevent="toggleRawResponse"> Show/hide </button>
          <div v-show="showRawResponse"><pre> {{ rawResp }}</pre> </div>
        </li>
        <li class="">
          Info: <ul>
            <li v-for="(c, k) in info" :key="k">{{ k }}: {{ c }}</li>
          </ul>
        </li>
      </ul>
      <div class="float-right">
        <button class="btn btn-secondary" @click.prevent="getServiceData"><i class="fas fa-redo" /></button>
      </div>
    </div>

    <div v-else class="col">
      <ServiceForm v-bind="{ onClickCancel: closeEdit, onSave: saveUpdate, originalUrl: url }"/>
    </div>
  </div>
</template>

<script>
import { getDataInfo } from "../services/requests"
import ServiceForm from "./ServiceForm"

export default {
  components: { ServiceForm },
  props: {
    info: {
      default: () => ({}),
      type: Object
    },
    name: {
      default: "",
      type: String
    },
    url: {
      default: "",
      type: String
    }
  },
  data() {
    return {
      edit: false,
      showRawResponse: false,
      internalUrl: this.url,
      rawResp: null
    }
  },
  computed: {
    inputUrl() {
      return `${this.name}-url`
    }
  },
  mounted() {
    this.getServiceData()
  },
  methods: {
    saveUpdate({ url }) {
      this.internalUrl = url
      this.toggleEdit()
    },
    cancelUpdate() {
      this.editUrl = this.url
      this.internalUrl = this.url
      this.toggleEdit()
    },
    toggleEdit() {
      this.edit = !this.edit
    },
    closeEdit() {
      this.edit = false
    },
    getServiceData() {
      getDataInfo(this.internalUrl).then(resp => {
        this.rawResp = resp
      })
    },
    toggleRawResponse() {
      this.showRawResponse = !this.showRawResponse
    }
  }
}
</script>

<style>
</style>
