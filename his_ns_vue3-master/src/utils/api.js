import axios from 'axios'

//let base = '/his';
let base='http://localhost:8080'
export const postReq = (url, params) => {
  return axios({
    method: 'post',
    url: `${base}${url}`,
    data: params,
	headers: {
	  'token': localStorage.getItem("token"),
	}
  });
}
export const getReq = (url,params) => {
  return  axios({
    method: 'get',
    data:params,
    url: `${base}${url}`,
	headers: {
	  'token': localStorage.getItem("token"),
	}
  });
}

export const fetchData = async (url, params) => {
  try {
    const response = await axios({
      method: 'get',
      params, // 修正：使用 params 而非 data
      url: `${base}${url}`,
      headers: {
        'token': localStorage.getItem('token'),
      }
    });
    return response.data;
  } catch (error) {
    console.error('请求失败:', error);
    throw error; // 修正：重新抛出错误，让调用者处理
  }
};



export const postJsonRequest = (url, params) => {
  return axios({
    method: 'post',
    url: `${base}${url}`,
    data: params,  // axios 自动将对象序列化为 JSON
    headers: {
      'Content-Type': 'application/json',
    }
  });
}


export const postFormRequest = (url, params) => {
  return axios({
    method: 'post',
    url: `${base}${url}`,
    data: params,
    transformRequest: [function (data) {
      let ret = '';
      for (let key in data) {
        ret += encodeURIComponent(key) + '=' + encodeURIComponent(data[key]) + '&';
      }
      return ret;
    }],
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    }
  });
}


export const uploadFileRequest = (url, params) => {
  // 创建 FormData 对象
  const formData = new FormData();
  
  // 将参数添加到 FormData 中
  Object.keys(params).forEach(key => {
    formData.append(key, params[key]);
  });
  
  return axios({
    method: 'post',
    url: `${base}${url}`,
    data: formData,
    headers: {
      // 不要设置 Content-Type，让浏览器自动处理
      // 'Content-Type': 'multipart/form-data'
    },
    // 可选：添加上传进度监控
    onUploadProgress: progressEvent => {
      const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
      console.log('上传进度:', percentCompleted);
      // 可以通过回调函数或状态管理库更新进度条
    }
  });
};


export const putRequest = (url, params) => {
  return axios({
    method: 'put',
    url: `${base}${url}`,
    data: params,
    transformRequest: [function (data) {
      if (!data) return '';
      
      const ret = [];
      for (let key in data) {
        if (data.hasOwnProperty(key)) {
          const value = data[key];
          // 处理值为 null 或 undefined 的情况
          const encodedValue = value === null || value === undefined ? '' : encodeURIComponent(value);
          ret.push(`${encodeURIComponent(key)}=${encodedValue}`);
        }
      }
      
      // 使用 join 避免末尾多余的 &
      return ret.join('&');
    }],
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
  .catch(error => {
    console.error('PUT 请求失败:', error);
    // 可以在这里进行统一的错误处理
    throw error; // 继续抛出错误，让调用者可以捕获
  });
};

export const deleteRequest = (url) => {
  return axios({
    method: 'delete',
    url: `${base}${url}`
  });
}

export const showConstants = (row, column, cellValue, index) => {
  let showMsg = '';
  let array = JSON.parse(localStorage.getItem('ConstantsObj'));
  array.forEach(function (c) {
    // console.log(c['ID']+":"+cellValue+":"+c['ConstantName']);
    if(c['ID']==cellValue){
      showMsg = c['ConstantName'];
    }
  });
  return showMsg;
}

export const showMsgTitle = (_this,msg) => {
  _this.$message(msg+"成功");
}
export const showMsgConfirm = (_this,msg) => {
    _this.$confirm('此操作将'+msg+',是否继续?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      _this.$message({
        type: 'success',
        message: '成功!'
      });
    }).catch(() => {
      _this.$message({
        type: 'info',
        message: '已取消'
      });
    });
}
