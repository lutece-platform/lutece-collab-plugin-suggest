/* Back office helpers of plugin-suggest */

function suggestShowWhenChecked( checkboxId, selector ) {
	var checkbox = document.getElementById( checkboxId );
	if ( !checkbox ) {
		return;
	}
	var update = function( ) {
		document.querySelectorAll( selector ).forEach( function( element ) {
			element.style.display = checkbox.checked ? '' : 'none';
		});
	};
	checkbox.addEventListener( 'change', update );
	update( );
}

function suggestEnableWhenChecked( fieldId, checkboxIds ) {
	var field = document.getElementById( fieldId );
	if ( !field ) {
		return;
	}
	var checkboxes = checkboxIds.map( function( id ) { return document.getElementById( id ); } ).filter( Boolean );
	var update = function( ) {
		field.disabled = !checkboxes.some( function( checkbox ) { return checkbox.checked; } );
	};
	checkboxes.forEach( function( checkbox ) { checkbox.addEventListener( 'change', update ); } );
	update( );
}

function suggestActivateTab( panelFieldId ) {
	var panelField = panelFieldId ? document.getElementById( panelFieldId ) : null;
	var target = window.location.hash.replace( '/', '' );
	if ( !target && panelField && panelField.value ) {
		target = panelField.value.charAt( 0 ) === '#' ? panelField.value : '#' + panelField.value;
	}
	document.querySelectorAll( '[data-bs-toggle="tab"]' ).forEach( function( link ) {
		var href = link.getAttribute( 'href' ) || link.getAttribute( 'data-bs-target' );
		if ( target && href === target && window.bootstrap ) {
			window.bootstrap.Tab.getOrCreateInstance( link ).show( );
		}
		link.addEventListener( 'shown.bs.tab', function( ) {
			if ( panelField ) {
				panelField.value = href.replace( '#', '' );
			}
		});
	});
}

function suggestToggleAll( buttonId, checkboxSelector ) {
	var button = document.getElementById( buttonId );
	if ( !button ) {
		return;
	}
	button.addEventListener( 'click', function( ) {
		var checked = button.getAttribute( 'data-is' ) === 'checked';
		document.querySelectorAll( checkboxSelector ).forEach( function( checkbox ) { checkbox.checked = checked; } );
		button.setAttribute( 'data-is', checked ? '' : 'checked' );
	});
}
