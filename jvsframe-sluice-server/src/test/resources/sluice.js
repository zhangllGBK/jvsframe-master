$(document).ready(function(){
    var serviceCode = $('.serviceCode span').text();
    var ruleHtml = '<tr>';
    ruleHtml += '<td><input type="text" name="rank" style="width:100px;" /></td>';
    ruleHtml += '<td><input type="text" name="exp" style="width:100%;" /></td>';
    ruleHtml += '<td><input type="text" name="errcode" style="width:100px;" /></td>';
    ruleHtml += '<td><input type="text" name="errdesc" style="width:100px;" /></td>';
    ruleHtml += '<td><input type="text" name="priority" style="width:100px;" /></td>';
    ruleHtml += '<td><input type="button" class="saveRule" value="保存"/></td>';
    ruleHtml += '</tr>';
    $('.addRule').click(function(e){
        $(e.target).parent().parent().before(ruleHtml);
        $('.saveRule').off('click');
        $('.saveRule').on('click', function(e1){
            var trEle = $(e1.target).parent().parent();
            var rank = $.trim(trEle.find('input[name="rank"]').val());
            var exp = $.trim(trEle.find('input[name="exp"]').val());
            var errcode = $.trim(trEle.find('input[name="errcode"]').val());
            var errdesc = $.trim(trEle.find('input[name="errdesc"]').val());
            var priority = $.trim(trEle.find('input[name="priority"]').val());
            console.log('rank=' + rank + ', exp=' + exp + ', errcode=' + errcode + ', errdesc=' + errdesc + ', priority=' + priority);
            $.post('00007.service', {'sulice': 'rule', 'serviceCode': serviceCode, 'rank': rank, 'exp': exp, 'errcode': errcode, 'errdesc': errdesc, 'priority': priority}, function(data){
                if (data == '0000') {
                    trEle.before('<tr><td>'+rank+'</td><td>'+exp+'</td><td>'+errcode+'</td><td>'+errdesc+'</td><td>'+priority+'</td><td> </td></tr>')
                    trEle.remove();
                    alert('添加规则成功');
                } else {
                    alert('添加规则失败');
                }
            });
        });
    });
    
    var variableHtml = '<tr>';
    variableHtml += '<td><input type="text" name="name" style="width:100px;" /></td>';
    variableHtml += '<td><input type="text" name="value" style="width:100%;" /></td>';
    variableHtml += '<td><input type="text" name="type" style="width:100px;" /></td>';
    variableHtml += '<td><input type="button" class="saveVariable" value="保存"/></td>';
    variableHtml += '</tr>';
    $('.addVariable').click(function(e){
        $(e.target).parent().parent().before(variableHtml);
        $('.saveVariable').off('click');
        $('.saveVariable').on('click', function(e1){
            var trEle = $(e1.target).parent().parent();
            var name = $.trim(trEle.find('input[name="name"]').val());
            var value = $.trim(trEle.find('input[name="value"]').val());
            var type = $.trim(trEle.find('input[name="type"]').val());
            console.log('name=' + name + ', value=' + value + ', type=' + type);
            $.post('00007.service', {'sulice': 'variable', 'serviceCode': serviceCode, 'name': name, 'value': value, 'type': type}, function(data){
                if (data == '0000') {
                    trEle.before('<tr><td>'+name+'</td><td>'+value+'</td><td>'+type+'</td><td> </td></tr>')
                    trEle.remove();
                    alert('添加变量成功');
                } else {
                    alert('添加变量失败');
                }
            });
        });
    });
    
    $('.delRule').click(function(e){
        var trEle = $(e.target).parent().parent();
        var ruleId = $(e.target).attr('data-value');
        $.post('00008.service', {'sulice': 'rule', 'serviceCode': serviceCode, 'ruleId': ruleId}, function(data){
            if (data == '0000') {
                trEle.remove();
                alert('删除规则成功');
            } else {
                alert('删除规则失败');
            }
        });
    });
    
    $('.delVariable').click(function(e){
        var trEle = $(e.target).parent().parent();
        var name = $(e.target).attr('data-value');
        $.post('00008.service', {'sulice': 'variable', 'serviceCode': serviceCode, 'name': name}, function(data){
            if (data == '0000') {
                trEle.remove();
                alert('删除变量成功');
            } else {
                alert('删除变量失败');
            }
        });
    });
    
});