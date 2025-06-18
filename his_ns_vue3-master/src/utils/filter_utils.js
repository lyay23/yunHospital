import Vue from 'vue'
import axios from 'axios'

Vue.filter("formatDate", function formatDate(value) {
  var date = new Date(value);
  var year = date.getFullYear();
  var month = date.getMonth() + 1;
  var day = date.getDate();
  if (month < 10) {
    month = "0" + month;
  }
  if (day < 10) {
    day = "0" + day;
  }
  return year + "-" + month + "-" + day;
});


Vue.filter("formatDateTime", function formatDateTime(value) {
  var date = new Date(value);
  var year = date.getFullYear();
  var month = date.getMonth() + 1;
  var day = date.getDate();
  var hours = date.getHours();
  var minutes = date.getMinutes();
  if (month < 10) {
    month = "0" + month;
  }
  if (day < 10) {
    day = "0" + day;
  }
  return year + "-" + month + "-" + day + " " + hours + ":" + minutes;
});


Vue.filter("formatConstant", function formatConstant(value) {
  let showMsg = '';
  let array = JSON.parse(localStorage.getItem('ConstantsObj'));
  array.forEach(function (c) {
    // console.log(c['ID']+":"+value+":"+c['ConstantName']);
    if(c['ID']==value){
      showMsg = c['ConstantName'];
    }
  });
  return showMsg;
});
