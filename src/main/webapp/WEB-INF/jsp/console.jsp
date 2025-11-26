<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
  String cp = request.getContextPath();
%>
<html>
<head>
    <title>DataTransfer 控制台</title>
    <script>
      let lostControl = false;
      function startService(type){
        fetch('<%=cp%>/service/'+type+'/start', {method: 'POST'})
        .then(resp=>resp.text()).then(msg=>{
          if(msg==='OK') alert('服務已啟動');
          else alert('依賴未完成或不可執行');
          refreshTable();
        });
      }
      function refreshTable(){
        fetch('<%=cp%>/status')
          .then(resp=>resp.json())
          .then(list=>{
            let tbody = document.getElementById('statusBody');
            tbody.innerHTML = '';
            list.forEach(svc=>{
              let row = `<tr>
                <td>${svc.displayName}</td>
                <td class="status-${svc.status}">${svc.status}</td>
                <td>
                  <button onclick="startService('${svc.type}')"
                    ${svc.enabled==='true'?'':'disabled'}>執行</button>
                </td>
              </tr>`;
              tbody.innerHTML += row;
            });
          });
      }
      function refreshControlStatus() {
        fetch('<%=cp%>/controller/status')
          .then(resp=>resp.json()).then(res=>{
            if(!res.isController) {
              if(!lostControl) {
                lostControl = true;
                alert('已失去操控權，將跳轉至只讀頁');
                window.location = '<%=cp%>/';
              }
            }
          });
      }
      setInterval(refreshTable, 5000);
      setInterval(refreshControlStatus, 8000); // 定時令牌狀態心跳
      window.onload=function(){
        refreshTable();
        refreshControlStatus();
      };
      window.onbeforeunload = function(){
        fetch('<%=cp%>/controller/release', {method:'POST'});
      };
    </script>
    <style>
      table { border-collapse: collapse; width: 60%; margin:auto;}
      th,td { border: 1px solid #aaa; padding:8px; text-align:center;}
      .status-NONE { background:#fafad2; }
      .status-START { background:#e6e6fa; }
      .status-RUNNING { background:#add8e6; }
      .status-FAIL { background:#f08080;}
      .status-DONE { background:#90ee90; }
      button:disabled { background: #cccccc;}
    </style>
</head>
<body>
    <h2 style="text-align:center;">DataTransfer控管台</h2>
    <table>
      <tr>
        <th>服務名稱</th><th>狀態</th><th>操作</th>
      </tr>
      <tbody id="statusBody"></tbody>
    </table>
</body>
</html>
