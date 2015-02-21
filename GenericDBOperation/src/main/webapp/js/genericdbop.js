
$(document).ready(function() {
	
		$('#downloadTemplate').on('change',function(){
			if($(this).prop('checked')){
				$('#dwnldspan').show();
				$('#uploadspan').hide();
			}else{
				$('#dwnldspan').hide();
				$('#uploadspan').show();
			}
		});
		
		$('#dwnldspan').on('click',function(){
        	if(!validation()){
        		return false;
        	}
			 var url  = "genericutil/dwnldtemplate/"+$('#tableName').val()+"/"+$('#operation').val()+".xml";
			 window.open(url ,"","resizable=yes, height=550, width=750, toolbar=no, menubar=no, status=no, location=no, scrollBars=no, target=blank");
		 });
		
		$('#CSV_UPLOAD_FORM').fileupload({
	    	dataType: 'json',
	        url:'genericutil',
	        done: function (e, data) {
	        	$("span#message").html(data.result['message']);
	        	if(data.result['result'] == 'success'){
	        		$("span#message").addClass("success");
	        	}else{
	        		$("span#message").addClass("error");
	        	}
	        	$("span#message").removeClass('hide');
	        	$('.progress-bar').css('width','100%');
	        	$("#progress").hide();
	        },
	        send : function(){
	        	$("#progress").hide();
	        	if(!validation()){
	        		return false;
	        	}
	            $("#progress").show();
	        },
	        formData : function(form){
	        	$("#progress").show();
	        	return form.serializeArray();
	        }
	    });
		
		function validation(){
        	if($('#operation').val()==""){
        		alert('Please select Operation');
        		return false;
        	}else if($('#tableName').val() =="none"){
        		alert('Please select table');
        		return false;
        	}
        	return true;
		}
});