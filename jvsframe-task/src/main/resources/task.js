$(document).ready(function(){
    var taskHtml = '<tr>';
    taskHtml += '<td><input type="text" name="code" style="width:100%;" /></td>';
    taskHtml += '<td><input type="text" name="sleeptime" style="width:100%;" /></td>';
    taskHtml += '<td></td>';
    taskHtml += '<td><input type="text" name="runtime" style="width:100%;" /></td>';
    taskHtml += '<td><input type="text" name="param" style="width:100%;" /></td>';
    taskHtml += '<td><input type="text" name="class" style="width:100%;" /></td>';
    taskHtml += '<td></td>';
    taskHtml += '<td><input type="button" class="saveTask" value="保存"/></td>';
    taskHtml += '</tr>';
    $('.addTask').click(function(e){
        $(e.target).parent().parent().before(taskHtml);
        $('.saveTask').off('click');
        $('.saveTask').on('click', function(e1){
            var trEle = $(e1.target).parent().parent();
            var code = $.trim(trEle.find('input[name="code"]').val());
            var sleeptime = $.trim(trEle.find('input[name="sleeptime"]').val());
            var runtime = $.trim(trEle.find('input[name="runtime"]').val());
            var param = $.trim(trEle.find('input[name="param"]').val());
            var clazz = $.trim(trEle.find('input[name="class"]').val());
            var node = $.trim(trEle.find('input[name="node"]').val());
            console.log('code=' + code + ', sleeptime=' + sleeptime + ', runtime=' + runtime + ', param=' + param + ', clazz=' + clazz+ ', node=' + node);
            $.post('00012.service', {'code': code, 'sleeptime': sleeptime, 'runtime': runtime, 'param': param, 'clazz': clazz, 'node': node}, function(data){
                if (data == '0000') {
                    trEle.before('<tr><td>'+code+'</td><td>'+sleeptime+'</td><td>Sleep</td><td>'+runtime+'</td><td>'+param+'</td><td>'+clazz+'</td><td>'+node+'</td><td><input type="button" value="删除" class="delTask"  data-value="' + code + '"  ></td></tr>')
                    trEle.remove();
                    alert('添加任务成功');
                } else {
                    alert('添加任务失败');
                }
            });
        });
    });
    $('.delTask').click(function(e){
        var trEle = $(e.target).parent().parent();
        var code = $(e.target).attr('data-value');
        $.post('00013.service', {'code': code}, function(data){
            if (data == '0000') {
                trEle.remove();
                alert('删除任务成功');
            } else {
                alert('删除任务失败');
            }
        });
    });
    $('.showlist').click(function(e){
        var val = $(e.target).next().val();
        alert(val);
    });
});