

kallinn ( kaffi , bull , drasl ) {
	var strato , mygla ;
	var party , ok ;
	strato = 100;
	while(strato > 10){
		writeln "ok";
		strato = strato - 1; 
	};
	party = kaffi+bull+drasl+strato;
	return party;
}
main() {
	var kona, boner;
	var nytt, furduleg, counter;
	kona = true;
	furduleg = false;
	boner = true;
	counter = 0;


	if  (kona || furduleg)
	{
		counter = counter +1;
		writeln counter;
	};
	if (kona &&  boner)
	{
		counter = counter +1;
		writeln counter;
	};
	if (!furduleg)
	{
		counter = counter +1;
		writeln counter;
	};
	writeln "fag";
	nytt = kallinn(1, 2, 3);
	writeln nytt;
	return kona;
}