function toggleRows(divId)
{
    if (document.getElementById)
    {
        var current = document.getElementById(divId).style;
        current.display = current.display == 'none' ? 'table-row-group' : 'none';

    }
    else if (document.all)
    {
        var current = document.all[divId].style;
        current.display = current.display ? "" : "table-row-group";
    }
}


function toggle(toggleId)
{
    var toggle;
    if (document.getElementById)
    {
        toggle = document.getElementById(toggleId);
    }
    else if (document.all)
    {
        toggle = document.all[toggleId];
    }
    toggle.textContent = toggle.innerHTML == '\u25b6' ? '\u25bc' : '\u25b6';
}