function flip(div)
{
    var current = div.style.display;
    if (current == 'block')
    {
        div.style.display = 'none';
    }
    else
    {
        div.style.display = 'block';
    }
}

function toggleDiv(div)
{
    if (document.getElementById)
    {
        flip(document.getElementById(div));
    }
    else if (document.all)
    {
        var current = document.all[div].style;
        current.display = current.display ? "" : "block";
    }
}
