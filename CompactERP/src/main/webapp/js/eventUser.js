/**
 * Created by Administrator on 2017/12/14.
 */
registerInit('eventUser', function (form) {
    //初始化树
    function initLeftTree() {
        postJson('/calendar/selectEventUser.do', {}, function (result) {
            $eventUserTree.jstree({
                'core': {
                    "multiple": true,
                    'data': result,
                    'dblclick_toggle': true,          //tree的双击展开
                    "check_callback": true        //
                },
                "plugins": ['search', 'checkbox']
            }).on("loaded.jstree", function (event, data) {
                $eventUserTree.jstree("deselect_all", true);
                if (!isEmpty(form.selectUsers)){
                    var selectedUsers = form.selectUsers;
                    $eventUserTree.jstree('select_node', selectedUsers, true, true);
                    $eventUserTree.jstree('close_all');
                }
            });
        });
    }
    var $eventUserTree = form.get().find('.event-user-tree');
    initLeftTree();
    //左侧search
    var to;
    var search = form.get().find('.event-user-search');
    search.keyup(function () {
        if (to) {
            clearTimeout(to);
        }
        to = setTimeout(function () {
            $eventUserTree.jstree(true).search(search.val());
        }, 250);
    });
    form.get().find('.event-user-save').on('click', function () {
        var nodes = $eventUserTree.jstree('get_selected');
        var users = {};
        if (!isEmpty(nodes)){
            for (var i=0; i<nodes.length; i++){
                var node = $eventUserTree.jstree('get_node', nodes[i]);
                if (isEmpty(node.data))
                    continue;
                users[node.data.id] = node.data;
            }
        }
        form.submit(users);
    });
});
