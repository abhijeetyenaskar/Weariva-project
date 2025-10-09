const menusidebar = document.getElementById("menusidebar");
const menubar = document.getElementById("menubar");
const usermenubar = document.getElementById("user-menu-bar");


// It handles menu bar active status
if (menubar && menusidebar) {
  menubar.onclick = () => {
    menusidebar.classList.toggle("w-96");
    menusidebar.classList.toggle("px-3");
    usermenubar.classList.remove("h-96");
    usermenubar.classList.remove("py-3");
  };
}

const usermenubutton = document.getElementById("user-menu-button");
// It handles user bar active status
if (usermenubutton && usermenubar) {
  usermenubutton.onclick = () => {
    usermenubar.classList.toggle("h-96");
    usermenubar.classList.toggle("py-3");
    menusidebar.classList.remove("w-96");
    menusidebar.classList.remove("px-3");
  };
}

const filterbtn = document.getElementById("filterbtn");
const filtersidebar = document.getElementById("filterSideBar");
const filterclose = document.getElementById("filterclose");
//  It handles filter active status
if (filterbtn && filterclose) {
  filterbtn.onclick = () => {
    filtersidebar.classList.add("active");
  };
  filterclose.onclick = () => {
    filtersidebar.classList.remove("active");
  };
}



