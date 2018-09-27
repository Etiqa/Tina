<template>
  <div class="container">
    <Header :logged="logged" :bc="bc" />

    <div class="row">
      <div class="col-md-12">
        Some example for various parsing functions
      </div>
    </div>

    <div class="row mt-5">
      <div class="col-md-12">
        <h3 class="h5">Basic</h3>
      </div>
      <pre class="col">
        <code class="JavaScript">
rawData => {
  return [
    ["Build Number", rawData.replace(/Build number/, "").replace(/\s/g, "")]
   ]
}
        </code>
      </pre>
    </div>

    <div class="row">
      <div class="col-md-12">
        <h3 class="h5">Version + build</h3>
      </div>
      <pre class="col">
        <code class="JavaScript">
rawData => {
  return [
    ["Version", rawData.replace(/Build number/, "").replace(/\s/g, "")],
    ["Build", rawData.trim().match(/\.[0-9]*$/, "")[0].replace(".", "")]
  ]
}
        </code>
      </pre>
    </div>

    <div class="row">
      <div class="col-md-12">
        <h3 class="h5">HTML page (repubblica.it)</h3>
      </div>
      <pre class="col">
        <code class="JavaScript">
rawData =&gt; {
  const shadowDom = document.createElement("div")
  shadowDom.innerHTML = rawData
    .match(/&lt;body[.\S\s]*&lt;\/body&gt;/)[0]
    .replace(/\n/g, "")
    .replace(/&lt;body&gt;/g, "")
    .replace(/&lt;\/body&gt;/g, "")
    .trim()

  return Array.prototype.slice
    .call(
      shadowDom.querySelectorAll(".first-page-left article .entry-title &gt; a")
    )
    .map(el =&gt; el.innerText.trim())
    .filter(el =&gt; {
      return el.match(/^[A-Z]/)
    })
}
        </code>
      </pre>
    </div>
  </div>
</template>

<script>
import Header from "../components/Header"
import hljs from "highlight.js"

export default {
  components: { Header },
  data() {
    return {
      logged: true,
      bc: [{ name: "Parse Functions" }]
    }
  },
  mounted() {
    hljs.initHighlightingOnLoad()
  }
}
</script>

<style>
</style>
