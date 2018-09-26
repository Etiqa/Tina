<template>
  <div class="container">
    <Header :logged="logged" />

    <div v-if="signin" class="row">
      <Signin v-bind="{ onToggleSignin }"/>
    </div>
    <div v-else class="row">
      <Signup v-bind="{ onToggleSignin }"/>
    </div>
  </div>
</template>

<script>
import Header from "../components/Header"
import Signin from "../components/Signin"
import Signup from "../components/Signup"

export default {
  components: { Header, Signin, Signup },
  data() {
    return {
      signin: false,
      logged: false
    }
  },
  methods: {
    onToggleSignin() {
      this.$set(this, "signin", !this.signin)
    }
  }
}
</script>

<style>
</style>
