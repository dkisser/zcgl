<template>
  <div>
    <group>
      <cell title="支付金额"></cell>
      <x-input v-model="total_fee" placeholder="请输入数字金额"></x-input>
      <x-button type="primary" @click.native="getScan">找人代付</x-button>
    </group>

    <div v-transfer-dom>
      <popup v-model="popShow" height="80%" :is-transparent="true">
        <div style="text-align: center">
          <qrcode :value="qrcodeUrl" type="img" :size=80 style="margin-top: 10px"></qrcode>
          <flexbox>
            <flexbox-item>
              <x-button type="primary" @click.native="goBcak">支付完成</x-button>
            </flexbox-item>
            <flexbox-item>
              <x-button type="warn" @click.native="goBcak">取消支付</x-button>
            </flexbox-item>
          </flexbox>
        </div>
      </popup>
    </div>

  </div>
</template>
<script>
  /* eslint-disable no-undef */
  import { TransferDom, Popup, Cell, Qrcode, XHeader, Toast, XInput, Group, XButton, Flexbox, FlexboxItem, PopupPicker } from 'vux'
  import api from '@/api/index'
  export default {
    directives: {
      TransferDom
    },
    components: {
      XInput,
      Cell,
      FlexboxItem,
      Toast,
      Flexbox,
      Qrcode,
      Group,
      XHeader,
      XButton,
      PopupPicker,
      Popup
    },
    methods: {
      getScan () {
        let that = this
        api.post('/wx/pay/getWxPayQRCode.do').then((response) => {
          that.qrcodeUrl = response
          that.popShow = !this.popShow
        })
      },
      goBcak () {
        this.$router.go(-1)
      }
    },
    data () {
      return {
        link: '',
        popShow: false,
        qrcodeUrl: '',
        total_fee: ''
      }
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
