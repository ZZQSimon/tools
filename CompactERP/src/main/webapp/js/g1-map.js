/**
 * Created with IntelliJ IDEA.
 * User: zang.loo
 * Date: 17-3-13
 * Time: 下午4:20
 */

"use strict";

var defaultMapTableOpts = {
	paging: false,
	order: [[1, 'asc']]
};
registerInit('map_select', function (form) {
    var field = w(form.parent);

	//function unselectRow($tr) {
	//	$tr.removeClass('selected');
	//	$tr.find('.dx-map-check').prop('checked', false);
	//}
    //
	//function selectRow($tr) {
	//	if ($tr.find('td.dataTables_empty').length > 0)
	//		return;
	//	$tr.addClass('selected');
	//	$tr.find('.dx-map-check').prop('checked', true);
	//}
    //
	//function toggleSelect($tr) {
	//	if ($tr.hasClass('selected'))
	//		unselectRow($tr);
	//	else
	//		selectRow($tr);
	//}
    //
	//function moveData(from, to) {
	//	var rows = from.rows('.selected');
	//	var data = rows.data();
	//	rows.remove().draw();
	//	var n = [];
	//	for (var i = 0; i < data.length; i++)
	//		n.push(data[i]);
	//	to.rows.add(n).draw();
	//}
    //
	//var $form = form.get();
	//$form.find('.dx-map-table tbody').on('click', 'tr', function () {
	//	toggleSelect($(this));
	//});
    //
	//$form.on('click', '.dx-map-checkall', function () {
	//	var func = this.checked ? selectRow : unselectRow;
	//	$(this).closest('table').find('tbody tr').each(function () {
	//		func($(this));
	//	});
	//});
    //
	//$form.find('.dx-map-save').on('click', function () {
	//	var data = {id: form.id, selection: {}};
	//	var rows = selection.rows().data();
	//	for (var i = 0; i < rows.length; i++)
	//		data.selection[rows[i].id] = rows[i].name;
	//	postJson('/widget/map/save.do', data, function () {
	//		var field = w(form.parent);
	//		field.updated(data.selection);
	//		form.p('formDialog').close();
	//	});
	//});
	//$form.find('.dx-map-add').on('click', function () {
	//	moveData(all, selection);
	//});
	//$form.find('.dx-map-remove').on('click', function () {
	//	moveData(selection, all);
	//});
    //
	//var columns = [{data: 'check', orderable: false}, {data: 'id'}];
	//if (form.nameText)
	//	columns.push({data: 'name'});
    //
	//var id = form.id + '-map-selected';
	//var opts = $.extend({
	//	dom: '<"H"<"#' + id + '.dx-table-toolbar">f>t',
	//	columns: columns,
	//	data: []
	//}, defaultMapTableOpts);
	//form.selection.forEach(function (id) {
	//	opts.data.push({check: '<input type="checkbox" class="dx-map-check"/>', id: id, name: form.all[id]});
	//});
	//var selection = $form.find("table.dx-map-selected").DataTable(opts);
	//$('#' + id).html('<label>' + msg('w.map.selected') + '</label>');
    //
	//id = form.id + '-map-available';
	//opts = $.extend({
	//	dom: '<"H"<"#' + id + '.dx-table-toolbar">f>t',
	//	columns: columns,
	//	data: []
	//}, defaultMapTableOpts);
	//forEach(form.all, function (id, name) {
	//	if ($.inArray(id, form.selection) >= 0)
	//		return;
	//	opts.data.push({check: '<input type="checkbox" class="dx-map-check"/>', id: id, name: name});
	//});
	//var all = $form.find("table.dx-map-all").DataTable(opts);
	//$("#" + id).html('<label>' + msg('w.map.available') + '</label>');
});