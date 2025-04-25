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

//時間帯で切り替え
  const hour = new Date().getHours();
  if (hour >= 5 && hour < 12) {
    document.body.classList.add("night"); //morning
  } else if (hour >= 12 && hour < 18) {
    document.body.classList.add("night"); //afternoon
  } else {
    document.body.classList.add("night");
  }
  

//明滅
   const numStars = 150;

    for (let i = 0; i < numStars; i++) {
      const star = document.createElement('div');
      star.classList.add('star');

      const size = Math.random() * 2 + 1; // 1px〜3px
      star.style.width = `${size}px`;
      star.style.height = `${size}px`;

      star.style.top = `${Math.random() * 100}vh`;
      star.style.left = `${Math.random() * 100}vw`;

      // 明滅タイミングをずらして自然に
      star.style.animationDelay = `${Math.random() * 4}s`;

      document.body.appendChild(star);
    }