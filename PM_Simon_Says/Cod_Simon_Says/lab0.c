#define F_CPU 16000000
#define ROUNDS 100
#define NR_OF_LEDS 8

#include <avr/io.h>
#include <avr/interrupt.h>
#include <util/delay.h>
#include <stdlib.h>
#include "lcd.h"

#define c	261
#define d	294
#define e	329
#define f	349
#define g	391
#define gS	415
#define a	440
#define aS	455
#define b	466
#define cH	523
#define cSH	554
#define dH	587
#define dSH	622
#define eH	659
#define fH	698
#define fSH	740
#define gH	784
#define gSH	830
#define aH	880

#define c4	c
#define d4	d
#define e4	e
#define f4	f
#define f4d	370
#define g4	g
#define a4	a	
#define b4	485
#define c5	cH

float notes[8] = {c4, d4, e4, f4, g4, a4, b4, c5};
float game_end_notes[4] = {d, d4, c, b};

void initTimer1()
{
	ICR1 = OCR1A = 0;
	DDRD |= (1 << PORTD5);
	
	TCCR1A = (1 << COM1A1) | (1 << WGM11);
	TCCR1B = (1 << WGM13) | (1 << WGM12) | (1 << CS11) | (1 << CS10);
}

void activate_ports(void)
{
	//pornire ecran
	LCD_init();

	/* Setez pinii PA ca iesiri. */
	DDRA |= (1 << PA0) | (1 << PA1) | (1 << PA2) | (1 << PA3) | (1 << PA4) | (1 << PA5) | (1 << PA6) | (1 << PA7);

	/* Sting LED-urile. */
	PORTA &= ~(1 << PA0) & ~(1 << PA1) & ~(1 << PA2) & ~(1 << PA3) & ~(1 << PA4) & ~(1 << PA5) & ~(1 << PA6) & ~(1 << PA7);

	/* Setez pinii PB ca intrari. */
	DDRB &= ~(1 << PB0) & ~(1 << PB1) & ~(1 << PB2) & ~(1 << PB3) & ~(1 << PB4) & ~(1 << PB5) & ~(1 << PB6) & ~(1 << PB7);

	/* Activez rezistenta de pull-up pentru pini. */
	PORTB |= (1 << PB0) | (1 << PB1) | (1 << PB2) | (1 << PB3) | (1 << PB4) | (1 << PB5) | (1 << PB6) | (1 << PB7);
	
	/* Setez buzzer-ul ca iesire */
	DDRD |= (1 << PORTD5);
	PORTD = 0;
}

void tone(float freq)
{
	DDRD |= (1 << PORTD5);

	ICR1 = 250000 * (1 / freq);
	OCR1A = ICR1 / 2;
}

void no_tone()
{
	ICR1 = OCR1A = 0;
	DDRD &= ~(1 << PORTD5);
}

void blink_led(int led_index)
{
	tone(notes[led_index]);
	
	switch (led_index)
	{
		case 0:
			PORTA |= (1 << PA0);
			_delay_ms(400);
			PORTA &= ~(1 << PA0);
			break;
			
		case 1:
			PORTA |= (1 << PA1);
			_delay_ms(400);
			PORTA &= ~(1 << PA1);
			break;
			
		case 2:
			PORTA |= (1 << PA2);
			_delay_ms(400);
			PORTA &= ~(1 << PA2);
			break;
			
		case 3:
			PORTA |= (1 << PA3);
			_delay_ms(400);
			PORTA &= ~(1 << PA3);
			break;
			
		case 4:
			PORTA |= (1 << PA4);
			_delay_ms(400);
			PORTA &= ~(1 << PA4);
			break;
			
		case 5:
			PORTA |= (1 << PA5);
			_delay_ms(400);
			PORTA &= ~(1 << PA5);
			break;
			
		case 6:
			PORTA |= (1 << PA6);
			_delay_ms(400);
			PORTA &= ~(1 << PA6);
			break;
			
		case 7:
			PORTA |= (1 << PA7);
			_delay_ms(400);
			PORTA &= ~(1 << PA7);
			break;
	}

	no_tone();
	_delay_ms(300);
}

void blink_all_leds(void)
{
	PORTA |= (1 << PA0) | (1 << PA1) | (1 << PA2) | (1 << PA3) | (1 << PA4) | (1 << PA5) | (1 << PA6) | (1 << PA7);
	_delay_ms(1000);

	PORTA &= ~(1 << PA0) & ~(1 << PA1) & ~(1 << PA2) & ~(1 << PA3) & ~(1 << PA4) & ~(1 << PA5) & ~(1 << PA6) & ~(1 << PA7);
	_delay_ms(500);
}

void end_game(char message[], char round_as_string[])
{
	int i;
	
	_delay_ms(500);
	
	for (i = 0; i < 4; i++) {
		tone(game_end_notes[i]);
		_delay_ms(300);
		no_tone();
	}

	while (1) {
		memset(message, 16, 0);
		strcpy(message, "Congratulations!");
		LCD_printAt(0, message);

		memset(message, 16, 0);
		strcpy(message, "Reached lvl ");
		strcat(message, round_as_string);
		LCD_printAt(0x40, message);
		
		blink_all_leds();
	}
}

void wait_input(int sequence[], int round, char message[], char round_as_string[])
{
	int i = 0, pressed_button;

	while (1)
	{
		pressed_button = -1;

		if ((PINB & (1 << PB0)) == 0)
			pressed_button = 0;
		else if ((PINB & (1 << PB1)) == 0)
			pressed_button = 1;
		if ((PINB & (1 << PB2)) == 0)
			pressed_button = 2;
		else if ((PINB & (1 << PB3)) == 0)
			pressed_button = 3;
		else if ((PINB & (1 << PB4)) == 0)
			pressed_button = 4;
		else if ((PINB & (1 << PB5)) == 0)
			pressed_button = 5;
		if ((PINB & (1 << PB6)) == 0)
			pressed_button = 6;
		else if ((PINB & (1 << PB7)) == 0)
			pressed_button = 7;
		
		/* Nu a fost apasat buton */
		if (pressed_button == -1)
			continue;
		
		blink_led(pressed_button);

		/* A fost gresita secventa */
		if (pressed_button != sequence[i])
			end_game(message, round_as_string);
		
		/* A fost redata intreaga secventa */
		if (i == round)
			break;

		i++;		
	}
}

void print_round(char message[], char round_as_string[])
{
	memset(message, 0, 16);

	strcpy(message, "Level ");
	strcat(message, round_as_string);
	
	LCD_printAt(0, message);
	
	strcpy(message ,"   Symon says  ");
	LCD_printAt(0x40, message);
}

void play(void)
{
	int round = 0, new_led, i;
	int sequence[ROUNDS];
	char message[16], round_as_string[3];
	no_tone();

	while(round < ROUNDS)
	{
		itoa(round + 1, round_as_string, 10);
		print_round(message, round_as_string);
		blink_all_leds();

		new_led = rand() % NR_OF_LEDS;
		sequence[round] = new_led;

		for (i = 0; i <= round; i++)
			blink_led(sequence[i]);

		wait_input(sequence, round, message, round_as_string);

		round++;
		_delay_ms(1000);
	}
}
	
int main() {
	srand(2000);
	sei();
	activate_ports();
	initTimer1();
	play();

	return 0;
}