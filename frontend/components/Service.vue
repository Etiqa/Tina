<template>
  <div class="col-md-12">
    <h3>{{ internalName }}</h3>

    <div v-if="!edit && !del">
      <div class="float-right">
        <button class="btn btn-primary" @click.prevent="toggleEdit"><i class="fas fa-pencil-alt" /></button>
        <button class="btn btn-danger" @click.prevent="startDelete"><i class="fas fa-ban"/></button>
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

    <div v-else-if="edit" class="col">
      <ServiceForm v-bind="{ onClickCancel: closeEdit, onSave: saveUpdate, originalUrl: url, originalName: name }"/>
    </div>
    <div v-else-if="del" class="col">
      <div>
        Do you really want to delete {{ internalName }}
      </div>
      <div>
        <button class="btn btn-danger" @click.prevent="deleteService">Delete</button>
        <button class="btn btn-primary" @click.prevent="stopDelete">Cancel</button>
      </div>
    </div>
  </div>
</template>

<script>
import { getDataInfo, updateService, deleteService } from "../services/requests"
import ServiceForm from "./ServiceForm"

export default {
  components: { ServiceForm },
  props: {
    id: {
      default: 999,
      type: Number
    },
    dirId: {
      default: "",
      type: String
    },
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
    },
    updateFn: {
      default: () => () => ({}),
      type: Function
    }
  },
  data() {
    return {
      edit: false,
      showRawResponse: false,
      internalUrl: this.url,
      internalName: this.name,
      rawResp: null,
      del: false
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
    saveUpdate({ url, name }) {
      this.internalUrl = url
      this.internalName = name
      this.toggleEdit()
      updateService(this.dirId, this.id, { url, name }).then(this.updateFn)
    },
    cancelUpdate() {
      this.editUrl = this.url
      this.internalUrl = this.url
      this.internalName = this.name
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
    },
    startDelete() {
      this.del = true
    },
    stopDelete() {
      this.del = false
    },
    deleteService() {
      deleteService(this.dirId, this.id).then(this.updateFn)
    }
  }
}
</script>

<style>
</style>
