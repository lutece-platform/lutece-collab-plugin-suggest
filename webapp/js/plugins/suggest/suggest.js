
function displayId(baliseId) {
	if (document.getElementById && document.getElementById(baliseId) != null) {
		document.getElementById(baliseId).style.visibility = 'visible';
		document.getElementById(baliseId).style.display = 'block';
	}
}

function hideId(baliseId) {
	if (document.getElementById && document.getElementById(baliseId) != null) {
		document.getElementById(baliseId).style.visibility = 'hidden';
		document.getElementById(baliseId).style.display = 'none';
	}
}

function getElementById(id) {
	if (document.getElementById)
		return document.getElementById(id);
	if (document.all)
		return eval("document.all." + id);
	return null;
}

function display(id, value) {
	var panel = getElementById(id);
	panel.style.display = value;
}

function doDisplay(id) {
	displayId("div" + id);
}
function hide(id) {
	hideId("div" + id);

}

function doCheckboxEffect(isChecked, id) {
	if (isChecked) {
		doDisplay(id);

	} else {
		hide(id);
	}
}
function initStarRating(form) {
	var radios = form.querySelectorAll('input.star-rating');
	var stars = [];
	radios.forEach(function (radio, index) {
		var star = document.createElement('span');
		star.className = 'suggest-star';
		star.setAttribute('role', 'button');
		star.setAttribute('tabindex', '0');
		star.setAttribute('title', radio.title);
		star.setAttribute('aria-label', radio.title);
		star.textContent = '\u2605';
		star.addEventListener('mouseenter', function () {
			stars.forEach(function (other, otherIndex) {
				other.classList.toggle('suggest-star-on', otherIndex <= index);
			});
		});
		star.addEventListener('mouseleave', function () {
			stars.forEach(function (other) {
				other.classList.remove('suggest-star-on');
			});
		});
		var vote = function () {
			form.elements.vote.value = radio.value;
			form.submit();
		};
		star.addEventListener('click', vote);
		star.addEventListener('keydown', function (event) {
			if (event.key === 'Enter' || event.key === ' ') {
				event.preventDefault();
				vote();
			}
		});
		radio.style.display = 'none';
		radio.parentNode.insertBefore(star, radio);
		stars.push(star);
	});
}

document.addEventListener('DOMContentLoaded', function () {
	var submitSort = document.getElementById('submit_sort');
	if (submitSort) {
		submitSort.style.display = 'none';
	}
	var sort = document.getElementById('id_suggest_submit_sort');
	var formSort = document.getElementById('form_sort');
	if (sort && formSort) {
		sort.addEventListener('change', function () {
			formSort.submit();
		});
	}
	document.querySelectorAll('.suggest-vote-star-rating-javascript-disable').forEach(function (element) {
		element.style.display = 'none';
	});
	document.querySelectorAll('.suggest-vote-star-rating').forEach(function (element) {
		element.style.display = '';
		element.querySelectorAll('form').forEach(initStarRating);
	});
});
