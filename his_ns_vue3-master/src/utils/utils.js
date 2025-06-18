export const isNotNullORBlank = (...args)=> {
  for (var i = 0; i < args.length; i++) {
    var argument = args[i];
    if (argument == null || argument == '' || argument == undefined) {
      return false;
    }
  }
  return true;
}

export const formatDateTime = (value) =>{
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
}




export const formatDate = (value) =>{
  var date = new Date(value);
  var year = date.getFullYear();
  var month = date.getMonth() + 1;
  var day = date.getDate();
  return year + "-" + month + "-" + day;
}

export const getDay = (val) =>{
	var date = new Date(Date.parse(val));
	switch (date.getDay()){
		case 0:return 7
		default:return date.getDay()
	}
}

export const getAge = (val) =>{
	var date = new Date(Date.parse(val));
	var now=new Date()
	return now.getFullYear()-date.getFullYear();
}

export const getTimeRange = () =>{
	var date=new Date()
	var hours = date.getHours();
	var minutes = date.getMinutes();
	var r=0
	switch(hours){
		case 7:
		case 8:
		case 14:r=1;break;
		case 9:
		case 15:r=3;break;
		case 10:
		case 16:r=5;break;
		case 11:
		case 17:r=8;break;
	}
	if(minutes>=30){
		r+=1;
	}
	return r;
}



export const isBefore = (val,val1) =>{
	var date = new Date(Date.parse(val));
	var date1 = new Date(Date.parse(val1));
	if(val<val1)
		return true;
	else
		return false
}

export const nextDay = (val) =>{
	var date = new Date(Date.parse(val));
	var year = date.getFullYear();
	var month = date.getMonth() + 1;
	var day = date.getDate()+1;
	 return year + "-" + month + "-" + day;
	
}
