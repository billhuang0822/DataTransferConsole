<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
  String cp = request.getContextPath();
%>
<html>
<head>
    <title>DataTransfer 監控台</title>
    <script>
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
              </tr>`;
              tbody.innerHTML += row;
            });
          });
      }
      setInterval(refreshTable, 5000);
      window.onload=refreshTable;
    </script>
    <style>
      table { border-collapse: collapse; width: 60%; margin:auto;}
      th,td { border: 1px solid #aaa; padding:8px; text-align:center;}
      .status-NONE { background:#fafad2; }
      .status-START { background:#e6e6fa; }
      .status-RUNNING { background:#add8e6; }
      .status-FAIL { background:#f08080;}
      .status-DONE { background:#90ee90; }
    </style>
</head>
<body>
    <h2 style="text-align:center;">DataTransfer監控台（只讀）</h2>
    <table>
      <tr>
        <th>服務名稱</th><th>狀態</th>
      </tr>
      <tbody id="statusBody"></tbody>
    </table>
</body>
</html>
