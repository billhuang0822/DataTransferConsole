<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>主控台</title>
    <script>
      function startService(type){
        fetch('/service/'+type+'/start', {method: 'POST'})
          .then(resp=>resp.text())
          .then(msg=>{
            alert(msg);
            location.reload();
          });
      }
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
    <h2 style="text-align:center;">服務主控台</h2>
    <table>
      <tr>
        <th>服務名稱</th><th>狀態</th><th>操作</th>
      </tr>
      <c:forEach var="svc" items="${services}">
        <tr>
          <td>${svc.type}</td>
          <td class="status-${svc.status}">${svc.status}</td>
          <td>
            <button 
                onclick="startService('${svc.type}')"
                <c:if test="${!serviceEnabled[svc.type]}">disabled</c:if>
            >執行</button>
          </td>
        </tr>
      </c:forEach>
    </table>
</body>
</html>