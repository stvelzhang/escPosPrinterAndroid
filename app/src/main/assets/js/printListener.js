document.write("<script language=javascript src='/js/Enums.js'></script>");

 <!--  打印监听-->
function getListenerState(msg){
     if(msg == ListenerState.SUCCESS){
       alert("打印成功");
     }else if(msg == ListenerState.NO_PAPER){
       alert("缺纸");
     }else if(msg == ListenerState.ERROR){
       alert("打印失败");
     }else if(msg == ListenerState.TEMPERATURE){
       alert("高温");
   }
}