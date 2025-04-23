const cursor = document.querySelector(".cursor");

document.addEventListener("mousemove", e => {
  cursor.style.top = e.clientY + "px";
  cursor.style.left = e.clientX + "px";
});

document.addEventListener("mousemove", e => {
  const trail = document.createElement("div");
  trail.className = "trail";
  trail.style.left = e.clientX + "px";
  trail.style.top = e.clientY + "px";
  document.body.appendChild(trail);

  setTimeout(() => {
    trail.remove();
  }, 300); // 300msで消す
});
