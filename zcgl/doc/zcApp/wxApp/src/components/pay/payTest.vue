<template>
  <div>
    <group>
      <cell title="支付金额"></cell>
      <x-input v-model="total_fee" placeholder="请输入数字金额"></x-input>
      <x-button type="primary" @click.native="publicPay">支付</x-button>
    </group>

  </div>
</template>
<script>
  /* eslint-disable no-undef */
  import { TransferDom, Popup, Cell, XSwitch, XHeader, Toast, XInput, Group, XButton, PopupPicker } from 'vux'
  import api from '@/api/index'
  export default {
    directives: {
      TransferDom
    },
    components: {
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
      publicPay () {
        let _this = this
        api.post('/wx/pay/publicPay.do', {price: _this.total_fee}).then((response) => {
          if (typeof WeixinJSBridge === 'undefined') {
            if (document.addEventListener) {
              document.addEventListener('WeixinJSBridgeReady', _this.onBridgeReady(response), false)
            } else if (document.attachEvent) {
              document.attachEvent('WeixinJSBridgeReady', _this.onBridgeReady(response))
              document.attachEvent('onWeixinJSBridgeReady', _this.onBridgeReady(response))
            }
          } else {
            _this.onBridgeReady(response)
          }
        })
      },
      onBridgeReady (data) {
        var vm = this
        WeixinJSBridge.invoke(
          'getBrandWCPayRequest', {
            'appId': data.appId,
            'timeStamp': data.timeStamp,
            'nonceStr': data.nonceStr,
            'package': data.package,
            'signType': data.signType,
            'paySign': data.paySign
          },
          function (res) {
            // 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回ok，但并不保证它绝对可靠。
            if (res.err_msg === 'get_brand_wcpay_request:ok') {
              vm.$router.go(-1)
            } else {
              this.$vux.toast.text('支付出现错误', 'bottom')
            }
          }
        )
      }
    },
    data () {
      return {
        link: '',
        popShow: false,
        path: '',
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
