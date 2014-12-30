Attribute VB_Name = "ModuleLinkProjectSlides"
Option Explicit


' Search for slide captions & link to the dst slides
Public Sub LinkSlidesText()
    Dim sl As Slide
    Set sl = ActiveWindow.View.Slide
    Dim slh As Dictionary
    Set slh = getSlidesByHeader
    linkSlideShapeText slh, sl.Shapes
End Sub

Public Function getSlidesByHeader() As Dictionary
    Dim d As Dictionary
    Set d = New Dictionary
    Dim sl As Slide
    For Each sl In ActivePresentation.Slides
        If sl.Shapes.Count > 0 Then
            Dim shp As Shape
            Set shp = sl.Shapes(1)
            If shp.HasTextFrame Then
                Dim sHeader$
                sHeader = Trim$(shp.TextFrame.TextRange.Text)
                'sHeader = Left$(sHeader, Len(sHeader) - 1)
                d.Add sHeader, sl
            End If
        End If
    Next sl
    Set getSlidesByHeader = d
End Function

Public Sub linkSlideShapeText(ByVal slh As Dictionary, _
                              ByVal shps) ' As Shapes
                              
    Dim shp As Shape
    For Each shp In shps
        'shp.Select: Stop
        
        If shp.HasTextFrame And shp.Id <> 2 Then
            With shp.TextFrame.TextRange
                 Dim t$: t = .Text
                 Dim k
                 For Each k In slh.Keys
                    Dim p As Long, l As Long
                    l = Len(k)
                    p = InStr(1, t, k)
                    Do Until p = 0
                        Dim sl As Slide
                        Set sl = slh(k)
                        With .Characters(p, l).ActionSettings(ppMouseClick).Hyperlink
                            .Address = ""
                            Dim sa$
                            sa = Join(Array(sl.SlideID, sl.SlideIndex, k), ",") ', sl.Name
                            .SubAddress = sa
                            '.TextToDisplay = k
                        End With
                        p = InStr(p + l, t, k)
                    Loop
                 Next k
            End With
        End If
        
        If shp.Type = msoGroup Then
            linkSlideShapeText slh, shp.GroupItems  ' (1)
        End If
    Next shp
End Sub
