<template>
  <div class="row">
    <form class="col" @submit.prevent="onSubmit">
      <div class="">
        <label for="url">Name</label>
        <input v-model="name" name="name" type="text" value="" class="form-control" required>
      </div>

      <div class="">
        <label for="url">Url</label>
        <input v-model="url" name="url" type="text" value="" class="form-control" required>
      </div>

      <div>
        <button class="btn btn-success" type="submit">Save</button>
        <button class="btn btn-primary" @click.prevent="onClickCancel">Cancel</button>
      </div>
    </form>
  </div>
</template>

<script>
export default {
  props: {
    onSave: {
      default: () => () => ({}),
      type: Function
    },
    onClickCancel: {
      default: () => () => ({}),
      type: Function
    },
    originalUrl: {
      default: () => null,
      type: String
    },
    originalName: {
      default: () => null,
      type: String
    }
  },
  data() {
    return {
      url: this.originalUrl,
      name: this.originalName
    }
  },
  methods: {
    onSubmit() {
      if (typeof this.onSave === "function") {
        this.onSave({ url: this.url, name: this.name })
      }
    }
  }
}
</script>

<style>
</style>
