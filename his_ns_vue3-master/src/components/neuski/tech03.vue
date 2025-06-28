<template>
  <!--   医技检查      -->
  <el-container style="height: 950px; border: 1px solid #eee">
    <!-- 页面弹窗 -->
    <el-dialog
      title="添加处置结果"
      :visible.sync="dialog01Visible"
      width="30%">
      <span>添加处置结果</span>
      <el-input placeholder="请输入处置结果内容，如果没有可不填写" type="textarea" rows='3' class="input-with-select" style="width: 100%">
        <template slot="prepend">疾病名称：</template>
        <el-button slot="append" icon="el-icon-search"></el-button>
      </el-input>
      <el-upload
        class="upload-demo"
        ref="upload"
        action="https://localhost:8080/his/upload01"
        :on-remove="handleRemove"
        :file-list="fileList01"
        :auto-upload="false">
        <el-button slot="trigger" size="small" type="primary">选取文件</el-button>
        <div slot="tip" class="el-upload__tip">选择处置结果图像，如果没有可以直接点击确认；只能上传jpg/png文件，且不超过500kb</div>
      </el-upload>
      <span slot="footer" class="dialog-footer">
      <el-button @click="dialog01Visible = false" size="small">取 消</el-button>
      <el-button style="margin-left: 10px;" size="small" type="success" @click="submitUpload">提交</el-button>
      </span>
    </el-dialog>
    <!-- 页面正文 -->
    <el-aside width="350px">
      <el-container >
        <el-header >
          <el-input placeholder="请输入内容"  class="input-with-select" style="width: 100%">
            <template slot="prepend">患者名：</template>
            <el-button slot="append" icon="el-icon-search"></el-button>
          </el-input>
        </el-header>
        <el-main style="height: 700px; border: 1px solid #eee ;padding: 0;margin: 0">
          <el-tabs type="border-card" >
            <el-tab-pane label="处置" >
              <el-tag>患者：</el-tag>
              <el-table ref="singleTable" :data="MyPatientsData01" highlight-current-row @current-change="handleCurrentChange01"
                        style="width: 100%" :show-header="false" size="mini">
                <el-table-column property="text01" label="病历号" >
                </el-table-column>
                <el-table-column property="text02" label="姓名" width="80">
                </el-table-column>
                <el-table-column property="text03" label="发票号" width="100">
                </el-table-column>
              </el-table><br/><br/>

            </el-tab-pane>
          </el-tabs>
        </el-main>
      </el-container>
    </el-aside>

    <el-container direction="vertical" style="height: 950px">
      <el-row style="background-color: #EAF1F5">
        <el-col :span="9" style="margin-top: 4px;">
          <el-tag size="mini">处置明细信息：</el-tag>
        </el-col>
        <el-col :span="5" >
          <el-button type="text" size="small" class="el-icon-success" @click="showConfirm('执行处置项')">执行确认</el-button>
        </el-col>
        <el-col :span="5" >
          <el-button type="text" size="small" class="el-icon-success" @click="showConfirm('取消处置项目')">取消执行</el-button>
        </el-col>
        <el-col :span="5" >
          <el-button type="text" size="small" class="el-icon-success" @click="dialog01Visible=true">填写结果</el-button>
        </el-col>
      </el-row>
      <el-form ref="form" :model="form01" label-width="80px" size="mini" :inline="true">
        <el-row>
          <div style="font-size:large;text-align: center" >处置明细单</div>
          <hr>
        </el-row>
        <el-row>
          <el-form-item label="病历号:" >
            <el-input type="text" size="mini" v-model="form01.text01" :span="8"></el-input>
          </el-form-item>
          <el-form-item label="患者姓名:" >
            <el-input type="text" size="mini" v-model="form01.text02" :span="8"></el-input>
          </el-form-item>
          <el-form-item label="年龄:" >
            <el-input type="text" size="mini" v-model="form01.text03" :span="8"></el-input>
          </el-form-item>
        </el-row>
        <el-row>
          <el-form-item label="结算类别:" >
            <el-input type="text" size="mini" v-model="form01.text04" :rows="8"></el-input>
          </el-form-item>
          <el-form-item label="就诊科室:" >
            <el-input type="text" size="mini" v-model="form01.text05" :rows="8"></el-input>
          </el-form-item>
          <el-form-item label="处方状态:" >
            <el-input type="text" size="mini" v-model="form01.text06" :rows="8"></el-input>
          </el-form-item>
        </el-row>
        <el-row>
          <el-form-item label="收费日期" >
            <el-input type="text" size="mini" v-model="form01.text07" :rows="8"></el-input>
          </el-form-item>
          <el-form-item label="开单医生" >
            <el-input type="text" size="mini" v-model="form01.text08" :rows="8"></el-input>
          </el-form-item>
          <el-form-item label="发票号" >
            <el-input type="text" size="mini" v-model="form01.text09" :rows="8"></el-input>
          </el-form-item>
        </el-row>
        <el-row style="background-color: #EAF1F5">
          <el-col :span="24" style="margin-top: 4px;">
            <el-tag size="mini">处置项信息：</el-tag>
          </el-col>
        </el-row>

        <el-row>
          <el-table :data="tableData01"    style="width: 100%">
            <el-table-column prop="choose01" label="" width="30px">
              <template slot-scope="scope">
                <input type="radio" name="chooseOne01" />
              </template>
            </el-table-column>
            <el-table-column prop="text01" label="处置项名称" >
            </el-table-column>
            <el-table-column prop="text02" label="部位" >
            </el-table-column>
            <el-table-column prop="text03" label="数量" width="50px">
            </el-table-column>
            <el-table-column prop="text04" label="单位" width="50px">
            </el-table-column>
            <el-table-column prop="text05" label="单价" width="50px">
            </el-table-column>
            <el-table-column prop="text06" label="总金额" width="70px">
            </el-table-column>
            <el-table-column prop="text07" label="状态" width="100px">
            </el-table-column>
          </el-table>
        </el-row>
      </el-form>

    </el-container>
  </el-container>
</template>
<style scoped>
  *{
    font-size: 14px;
    font-weight: 500;
    font-family: "Microsoft YaHei","微软雅黑","Helvetica Neue",Helvetica,"PingFang SC","Hiragino Sans GB",Arial,sans-serif;;
  }
</style>

<script>
  import {showMsgTitle} from '../../utils/api'
  import  {showMsgConfirm} from '../../utils/api'
  import {formatDateTime} from "../../utils/utils";

  export default {
    name: 'tabpage',
    data() {
      return {
        dialog01Visible:false,
        fileList01: [{
          name: 'food.jpeg',
          url: 'https://localhost:8080/his/upload01/3/63/4e7f3a15429bfda99bce42a18cdd1jpeg.jpeg?imageMogr2/thumbnail/360x360/format/webp/quality/100'
        }, {
          name: 'food2.jpeg',
          url: 'https://localhost:8080/his/upload01/3/63/4e7f3a15429bfda99bce42a18cdd1jpeg.jpeg?imageMogr2/thumbnail/360x360/format/webp/quality/100'
        }],
        form01:{
          text01: '',
          text02: '',
          text03: '',
          text04: '',
          text05: '',
          text06: '',
          text07: '',
          text08: '',
          text09: '',
        },
        MyPatientsData01: [{
          text01: '01212124545',
          text02: '易慧丹',
          text03: '3322'
        }, {
          text01: '01212124546',
          text02: '桂龙成',
          text03: '41315'
        }, {
          text01: '01212124547',
          text02: '董晨发',
          text03: '213211'
        }],
        MyPatientsData02: [{
          MedicalNo: '01212124542',
          name: '张金龙',
          age: '432135'
        }, {
          MedicalNo: '01212124543',
          name: '王玉荣',
          age: '43217'
        }, {
          MedicalNo: '01212124544',
          name: '邓桂英',
          age: '232139'
        }],
        tableData01:[{
          text01:'洗胃',
          text02:'',
          text03:'1',
          text04:'次',
          text05:'40',
          text06:'40',
          text07:'未执行',
        },{
          text01:'导尿',
          text02:'',
          text03:'1',
          text04:'次',
          text05:'10',
          text06:'10',
          text07:'未执行',
        },{
          text01:'吸痰护理',
          text02:'',
          text03:'1',
          text04:'次',
          text05:'15',
          text06:'15',
          text07:'未执行',
        },]
      }
    },
    methods: {
      submitUpload() {
        this.$refs.upload.submit();
        this.dialog01Visible=false;
        showMsgTitle(this, '填写结果');
      },
      handleRemove(file, fileList) {
        showMsgTitle(this, '移除图片'+file.name);
      },
      handleCurrentChange01(val) {
        this.form01.text01 = val.text01;
        this.form01.text02 = val.text02;
        this.form01.text03 = '31';
        this.form01.text04 = '自费';
        this.form01.text07 = formatDateTime(new Date().getTime());
        this.form01.text09 = val.text03;
      },
      showConfirm(msg) {
        showMsgConfirm(this, msg);
      },
      showMsg(msg) {
        showMsgTitle(this, msg);
      },
    }
  }
</script>
