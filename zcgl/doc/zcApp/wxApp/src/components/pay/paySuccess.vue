<template>
  <div>
    <group>
      <cell title="支付成功"></cell>
      <x-button type="primary" @click.native="back">确认</x-button>
    </group>
  </div>
</template>
<script>
  /* eslint-disable no-undef */
  import { TransferDom, Cell, XSwitch, XHeader, Toast, XInput, Popup, Group, XButton, PopupPicker, Actionsheet } from 'vux'
  export default {
    components: {
      TransferDom,
      Actionsheet,
      XInput,
      Cell,
      Toast,
      XSwitch,
      Group,
      XHeader,
      XButton,
      PopupPicker,
      Popup
    },
    methods: {
      back () {
        this.$router.go(-2)
      }
    },
    data () {
    },
    mounted: function () {
    },
    beforeRouteEnter (to, from, next) {
      next(vm => {
        vm.$store.dispatch('setNavHeadTitle', to.query.title)
      })
    },
    beforeRouteLeave (to, from, next) {
      next()
    }
  }
</script>
<style scoped>
  .textTop{
    vertical-align: top;
  }
  .textTop p{
    font-size: 14px;
    font-size: 0.88rem;
  }
  .dateCls{
    width: 68px;
  }
  .inlineStyle{
    display: inline;
  }
  .ml{
    margin-left: 2px;
  }
  .scan {
    margin: auto;
    position: absolute;
    top: 0; left: 0; bottom: 0; right: 0;
  }
</style>
