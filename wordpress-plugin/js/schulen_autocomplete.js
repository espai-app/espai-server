function autocomplete(input, suggestService) {
  /*the autocomplete function takes two arguments,
   the text field element and an array of possible autocompleted values:*/
  var currentFocus;
  /*execute a function when someone writes in the text field:*/
  input.addEventListener("input", async function (e) {

    /*close any already open lists of autocompleted values*/
    closeAllLists();
    if (!this.value) {
      return false;
    }
    currentFocus = -1;
    /*create a DIV element that will contain the items (values):*/
    var listWrapper = document.createElement("div");
    listWrapper.setAttribute("id", this.id + "autocomplete-list");
    listWrapper.setAttribute("class", "autocomplete-items");
    /*append the DIV element as a child of the autocomplete container:*/
    this.parentNode.appendChild(listWrapper);
    
    keyword = this.value.replace(/&=%\?:\*/g, "");

    if (keyword.length < 3) {
      return;
    }
    response = await fetch(suggestService + "?q=" + keyword, {"mode": "cors", "cache": "no-cache"});
    if (!response.ok) {
      closeAllLists();
      return false;
    }
    searchResult = await response.json();
    docs = searchResult.response.docs;

    for (const doc of docs) {
      var listItem = document.createElement("div");
      listItem.innerHTML = "<strong>" + doc.name + "</strong><br />" + doc.address + "<br \>" + doc.postcode + " " + doc.city;
      listItem.dataset.name = doc.name;
      listItem.dataset.address = doc.address;
      listItem.dataset.postcode = doc.postcode;
      listItem.dataset.city = doc.city;
      listItem.dataset.schoolType = doc.school_type;

      listItem.addEventListener("click", function (e) {
        input.value = this.dataset.name;
        document.getElementById("address").value = this.dataset.address;
        document.getElementById("postcode").value = this.dataset.postcode;
        document.getElementById("city").value = this.dataset.city;
        document.getElementById("schooltype").value = this.dataset.schoolType;
        
        closeAllLists();
      });

      listWrapper.append(listItem);
    }
  });
  /*execute a function presses a key on the keyboard:*/
  input.addEventListener("keydown", function (e) {
    var x = document.getElementById(this.id + "autocomplete-list");
    if (x)
      x = x.getElementsByTagName("div");
    if (e.keyCode == 40) {
      /*If the arrow DOWN key is pressed,
       increase the currentFocus variable:*/
      currentFocus++;
      /*and and make the current item more visible:*/
      addActive(x);
    } else if (e.keyCode == 38) { //up
      /*If the arrow UP key is pressed,
       decrease the currentFocus variable:*/
      currentFocus--;
      /*and and make the current item more visible:*/
      addActive(x);
    } else if (e.keyCode == 13) {
      /*If the ENTER key is pressed, prevent the form from being submitted,*/
      e.preventDefault();
      if (currentFocus > -1) {
        /*and simulate a click on the "active" item:*/
        if (x)
          x[currentFocus].click();
      }
    } else if (e.keyCode == 9 || e.keyCode == 27) {
      closeAllLists();
    }
  });
  function addActive(x) {
    /*a function to classify an item as "active":*/
    if (!x)
      return false;
    /*start by removing the "active" class on all items:*/
    removeActive(x);
    if (currentFocus >= x.length)
      currentFocus = 0;
    if (currentFocus < 0)
      currentFocus = (x.length - 1);
    /*add class "autocomplete-active":*/
    x[currentFocus].classList.add("autocomplete-active");
  }
  function removeActive(x) {
    /*a function to remove the "active" class from all autocomplete items:*/
    for (var i = 0; i < x.length; i++) {
      x[i].classList.remove("autocomplete-active");
    }
  }
  function closeAllLists(elmnt) {
    /*close all autocomplete lists in the document,
     except the one passed as an argument:*/
    var x = document.getElementsByClassName("autocomplete-items");
    for (var i = 0; i < x.length; i++) {
      if (elmnt != x[i] && elmnt != input) {
        x[i].parentNode.removeChild(x[i]);
      }
    }
  }
  /*execute a function when someone clicks in the document:*/
  document.addEventListener("click", function (e) {
    closeAllLists(e.target);
  });
}
