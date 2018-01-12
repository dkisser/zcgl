<template>
  <div>
    <div v-for="item in recordList">
      <group>
        <table>
          <tbody>
          <tr>
            <td class="textTop dateCls">
              <h3 class="inlineStyle">{{item.fssj | normalTime('DD')}}</h3><h5 class="inlineStyle ml">{{item.fssj | normalTime('MM月')}}</h5>
            </td>
            <td>
              {{item.nr}}
            </td>
          </tr>
          </tbody>
        </table>
      </group>
    </div>
  </div>
</template>

<script>
  import { XSwitch, Group, XButton, PopupPicker } from 'vux'
  export default {
    components: {
      XSwitch,
      Group,
      XButton,
      PopupPicker
    },
    methods: {
    },
    data () {
      return {
        recordList: [
          {
            fssj: 1505232000000,
            nr: '于2017-09-13因交党费支付5元'
          },
          {
            fssj: 1505232000000,
            nr: '于2017-09-13因交修理资产00035支付23元'
          },
          {
            fssj: 1505232000000,
            nr: '于2017-09-13因修理资产00039支付200元'
          }
        ]
      }
    },
    mounted: function () {
    },
    beforeRouteEnter (to, from, next) {
      next(vm => {
        // 我的消息
        vm.$store.dispatch('setNavHeadTitle', '我的支付')
        // hidebottomtab
      })
    },
    beforeRouteLeave (to, from, next) {
      // showbottomtab
      next()
    }
  }
</script>
<style scoped>
  .textTop {
    vertical-align: top;
  }

  .textTop p {
    font-size: 10px;
  }

  .ttt {
    font-size: 10px;
  }

  .dateCls {
    width: 68px;
  }

  .inlineStyle {
    display: inline;
  }

  .ml {
    margin-left: 2px;
  }
</style>
