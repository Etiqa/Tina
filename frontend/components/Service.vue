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
          Parse Function:
          <button class="btn btn-success" @click.prevent="toggleParseFn"> Show/hide </button>
          <div v-show="showParseFn"><pre> {{ internalParseFn }}</pre> </div>
        </li>
        <li class="col-md-6">
          Info
          <table class="table table-striped">
            <tr v-for="(c, k) in info" :key="k">
              <div v-if="typeof c === 'object'">
                <td v-for="(cc, kk) in c" :key="kk">{{ cc }}</td>
              </div>
              <div v-else>
                <td>{{ c }}</td>
              </div>
            </tr>
          </table>
        </li>
      </ul>
      <div class="float-right">
        <button class="btn btn-secondary" @click.prevent="getServiceData"><i class="fas fa-redo" /></button>
      </div>
    </div>
    <div v-else-if="edit" class="col">
      <ServiceForm v-bind="{ onClickCancel: closeEdit, onSave: saveUpdate, originalUrl: url, originalName: name, originalParseFn: internalParseFn }"/>
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
    },
    parseFn: {
      default: "",
      type: String
    }
  },
  data() {
    return {
      edit: false,
      showRawResponse: false,
      showParseFn: false,
      rawResp: null,
      del: false
    }
  },
  computed: {
    inputUrl() {
      return `${this.name}-url`
    },
    info() {
      const parseFn =
        this.parseFn && typeof eval(this.parseFn) === "function"
          ? eval(this.parseFn)
          : raw => [raw]
      return this.rawResp ? parseFn(this.rawResp) : []
    },
    internalName() {
      return this.name
    },
    internalUrl() {
      return this.url
    },
    internalParseFn() {
      console.log(this.parseFn)
      return this.parseFn
    }
  },
  mounted() {
    this.getServiceData()
  },
  methods: {
    saveUpdate({ url, name, parseFn }) {
      this.toggleEdit()
      updateService(this.dirId, this.id, { url, name, parseFn }).then(
        this.updateFn
      )
    },
    cancelUpdate() {
      this.editUrl = this.url
      this.internalUrl = this.url
      this.internalName = this.name
      this.internalParseFn = this.parseFn
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
    toggleParseFn() {
      this.showParseFn = !this.showParseFn
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
