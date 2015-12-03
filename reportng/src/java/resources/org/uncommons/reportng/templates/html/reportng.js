function toggleElement(elementId, displayStyle) {
	var element = document.getElementById(elementId);
	var current = element.currentStyle ? element.currentStyle['display']
			: document.defaultView.getComputedStyle(element, null)
					.getPropertyValue('display');
	element.style.display = (current == 'none' ? displayStyle : 'none');
}

function toggle(toggleId) {
	var toggle = document.getElementById ? document.getElementById(toggleId)
			: document.all[toggleId];
	toggle.textContent = toggle.innerHTML == '\u25b6' ? '\u25bc' : '\u25b6';
}

function loadExternal(elementID, src) {
	var element = document.getElementById(elementID)
	if ("".indexOf(element.innerHTML) == 0) {
		element.innerHTML = "Loading...";
		var client = new XMLHttpRequest();
		client.open('GET', src);
		client.onreadystatechange = function() {
			element.innerHTML = "<pre>" + client.responseText +"</pre>";
		}
		client.send();
	}
}