package com.example.marcin.osmtest;

/**
 * Created by Marcin on 13.11.2016.
 *
 * mapowanie wykorzystywane przez OSRM
 * przypisanie odpowiednim manewrom ich IDkow
 */
 enum ManeuverType
{
    newname(2),
    turnstraight(1),
   turnslightright(6),
   turnright(7),
   turnsharpright(8),
   turnuturn(12),
   turnsharpleft(5),
   turnleft(4),
   turnslightleft(3),
   depart(24),
   arrive(24),
   roundabout1(27),
   roundabout2(28),
   roundabout3(29),
   roundabout4(30),
   roundabout5(31),
   roundabout6(32),
   roundabout7(33),
   roundabout8(34),
   mergeleft(20),
   mergesharpleft(20),
   mergeslightleft(20),
   mergeright(21),
   mergesharpright(21),
   mergeslightright(21),
   mergestraight(22),
   rampleft(17),
   rampsharpleft(17),
   rampslightleft(17),
   rampright(18),
   rampsharpright(18),
   rampslightright(18),
   rampstraigh(19);

    private int value;
    public int getValue()
    {
        return value;
    }
    ManeuverType(int value)
    {
        this.value = value;
    }
}
