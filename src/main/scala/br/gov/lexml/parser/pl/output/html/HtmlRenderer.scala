package br.gov.lexml.parser.pl.output.html
import scala.xml._
import br.gov.lexml.parser.pl._
import br.gov.lexml.parser.pl.rotulo._
import br.gov.lexml.parser.pl.block._

object HtmlRenderer {
	def rename(label : String,n : NodeSeq) : NodeSeq = n match {
		case Elem(prefix,_, attributes, scope, child@_*) =>
				Elem(prefix, label, attributes, scope, true, child:_*)
		case _ => n
	}
	
	def elemLabel(r : Rotulo) : String = r match {
		case _ : RotuloArtigo => "Artigo"
		case RotuloParagrafo(None,_,_) => "Caput"
		case _ : RotuloParagrafo => "Paragrafo"
		case _ : RotuloInciso => "Inciso"
		case _ : RotuloAlinea => "Alinea"
		case _ : RotuloItem => "Item"
		case RotuloPena => "Pena"
		case _ : RotuloParte => "Parte"
		case _ : RotuloLivro => "Livro"
		case _ : RotuloTitulo => "Titulo"
		case _ : RotuloSubTitulo => throw new RenderException("Sub-título não suportado pelo parser")
		case _ : RotuloCapitulo => "Capitulo"
		case _ : RotuloSubCapitulo => throw new RenderException("Sub-capítulo não suportado pelo parser")
		case _ : RotuloSecao => "Secao"
		case _ : RotuloSubSecao => "SubSecao"
		case _ : RotuloAlteracao => "Alteracao"
    case x => throw new RuntimeException("Lexml Xml renderer. Elemento não esperado:" + x)
	}
	
	def renderNumeral(num : Int) : String = {
		if (num > 1000) { "%s.%03d".format(renderNumeral (num / 1000),num % 100) } 
		else { num.toString }		
	}
	
	def renderOrdinal(num : Int) : String = renderNumeral(num) + (if (num < 10) "º"  else "") 		
	
	def renderComp(onum : Option[Int]) : String = onum.map("-" + renderAlphaSeq(_).toUpperCase).getOrElse("")
			
	def renderRomano(num : Int) : String = {
		def rom(cX : String, cV : String,cI : String, d : Int) : String = d match {
			case 0 => ""
			case 9 => cI + cX
			case 4 => cI + cV
			case _ if d >= 5 => cV + (cI * (d - 5))
			case _ => cI * d							
		}		
		("M" * (num / 1000)) + rom("M","D","C",(num / 100) % 10) + rom("C","L","X",(num /10) % 10) + rom("X","V","I",num % 10)		
	}
	
	def renderAlphaSeq(num : Int) : String = {
		def rend(n : Int) : String = n match {
			case 0 => "" 
			case _ => {
				val nn = n - 1
				rend(nn/26) + ('a' + (nn % 26)).asInstanceOf[Char]
			}
		}
		rend(num+1)		
	}

	class RenderException(msg : String) extends RuntimeException(msg)			
	
	def renderRotulo(r : Rotulo) : NodeSeq = r match {	
		case RotuloArtigo(1,None,true) => <span class="rotuloDispositivo">Artigo </span> <span class="numeroDispositivo">único.</span>
		case RotuloArtigo(num,comp,_) => <span class="rotuloDispositivo">Artigo </span> <span class="numeroDispositivo">{renderOrdinal(num) + renderComp(comp) + (if (num>=10 || comp.isDefined) "."  else "")}</span>
		case RotuloParagrafo(None,_,_) => <span class="rotuloDispositivo">Caput.</span>
		case RotuloParagrafo(Some(1),None,true) => <span class="rotuloDispositivo">Parágrafo </span> <span class="numeroDispositivo">único.</span>
		case RotuloParagrafo(Some(num),comp,_) => <span class="rotuloDispositivo">Parágrafo </span> <span class="numeroDispositivo">{renderOrdinal(num) + renderComp(comp) + (if (num>=10 || comp.isDefined) "."  else "")}</span>			
		case RotuloInciso(num, comp) => <span class="rotuloDispositivo">Inciso </span> <span class="numeroDispositivo">{renderRomano(num).toUpperCase + renderComp(comp)} –</span> 
		case RotuloAlinea(num, comp) => <span class="rotuloDispositivo">Alínea </span> <span class="numeroDispositivo">{renderAlphaSeq(num-1).toLowerCase + renderComp(comp)})</span>
		case RotuloItem(num,comp) => <span class="rotuloDispositivo">Item </span> <span class="numeroDispositivo">{num.toString} –</span>
		case RotuloPena => <span class="rotuloDispositivo">Pena –</span>
		case RotuloParte(Left(_),_) => throw new RenderException("Parte sem número não suportado na renderização")
		case RotuloParte(Right(num),comp) => <span class="rotuloDispositivo">PARTE </span> <span class="numeroDispositivo">{renderRomano(num).toUpperCase + renderComp(comp)}</span>
		case RotuloLivro(Left(_),_) => throw new RenderException("Livro sem número não suportado na renderização")
		case RotuloLivro(Right(num),comp) => <span class="rotuloDispositivo">LIVRO </span><span class="numeroDispositivo">{renderRomano(num).toUpperCase + renderComp(comp)}</span>
		case RotuloTitulo(num, comp) => <span class="rotuloDispositivo">TÍTULO </span><span class="numeroDispositivo">{renderRomano(num) + renderComp(comp)}</span>
		case RotuloSubTitulo(num, comp) => <span class="rotuloDispositivo">SUB-TÍTULO </span> <span class="numeroDispositivo">{renderRomano(num) + renderComp(comp)}</span>
		case RotuloCapitulo(num, comp) => <span class="rotuloDispositivo">CAPÍTULO </span> <span class="numeroDispositivo">{renderRomano(num) + renderComp(comp)}</span>
		case RotuloSubCapitulo(num, comp) => <span class="rotuloDispositivo">SUB-CAPÍTULO </span> <span class="numeroDispositivo">{renderRomano(num) + renderComp(comp)}</span>
		case RotuloSecao(num, comp) => <span class="rotuloDispositivo">SEÇÃO </span> <span class="numeroDispositivo">{renderRomano(num) + renderComp(comp)}</span>
		case RotuloSubSecao(num, comp) => <span class="rotuloDispositivo">SUBSEÇÃO </span> <span class="numeroDispositivo">{renderRomano(num) + renderComp(comp)}</span>
		case RotuloAlteracao(num) => <span class="rotuloDispositivo">Alteracao </span> <span class="numeroDispositivo">{num.toString}</span>	
    case x => throw new RuntimeException("Lexml Xml renderer. Elemento não esperado:" + x)
  } 
					
	def renderCompId(n : Option[Int]) = n.map("-" + _.toString).getOrElse("")
	
	def renderId(r : Rotulo) : String = r match {
		case RotuloArtigo(num,comp,_) => "art%d%s" format(num,renderCompId(comp))
		case RotuloParagrafo(None,_,_) => "cpt"		
		case RotuloParagrafo(Some(num),comp,_) => "par%d%s" format(num,renderCompId(comp))			
		case RotuloInciso(num, comp) => "inc%d%s" format(num,renderCompId(comp))
		case RotuloAlinea(num, comp) => "ali%d%s" format(num,renderCompId(comp))
		case RotuloItem(num,comp) => "ite%d%s" format(num,renderCompId(comp))
		case RotuloPena => "pena"
		case RotuloParte(Left(_),_) => throw new RenderException("Parte sem número não suportado na renderização")
		case RotuloParte(Right(num),comp) => "prt%d%s" format(num,renderCompId(comp))
		case RotuloLivro(Left(_),_) => throw new RenderException("Livro sem número não suportado na renderização")
		case RotuloLivro(Right(num),comp) => "liv%d%s" format(num,renderCompId(comp))
		case RotuloTitulo(num, comp) => "tit%d%s" format(num,renderCompId(comp))
		case RotuloSubTitulo(num, comp) => throw new RenderException("Sub-título não suportado pelo parser")
		case RotuloCapitulo(num, comp) => "cap%d%s" format(num,renderCompId(comp))
		case RotuloSubCapitulo(num, comp) => throw new RenderException("Sub-capítulo não suportado pelo parser")
		case RotuloSecao(num, comp) => "sec%d%s" format(num,renderCompId(comp))
		case RotuloSubSecao(num, comp) => "sub%d%s" format(num,renderCompId(comp))
		case RotuloAlteracao(num) => "alt%d" format(num)
    case x => throw new RuntimeException("Lexml Xml renderer. Elemento não esperado:" + x)
	}
	
	def renderId(path : List[Rotulo]) : String = path.reverse.map(renderId).mkString("","_","")		
			
	def render(idPai : String) : ((Block,Int)) => NodeSeq = { case (b :  Block,idx : Int) => b match {	
			case d : Dispositivo => {
				val id = d.id				
				def mid(prefix : String)  = prefix + "_" + id
				(<div class={"dispositivo " + elemLabel(d.rotulo).toLowerCase} id={mid("div")}>
						{
						  d.titulo.map(ti => <div class="tituloDispositivo">{ti.nodes}</div>).getOrElse(NodeSeq.Empty)
						}
			
						<div class="divRotuloDispositivo">{renderRotulo(d.rotulo)}</div>
						{
						  d.conteudo match {
						    case None => NodeSeq.Empty
						    case Some(x) => <div class="conteudoDispositivo">{ x match {
						    	case Omissis(_,_,_) => <span class="omissis">...</span>
						    	case Paragraph(ns,_) => <span class="texto">{ns}</span>
						    	case _ => <span class="erro">não esperado: { x.toString }</span>
						    } }</div>
						  }
						}
						{
						  d.subDispositivos match {
						    case Nil => NodeSeq.Empty
						    case _ => <div class="subDispositivos">{
						    	NodeSeq fromSeq d.subDispositivos.zipWithIndex.flatMap(render(id))
						    }</div>
						  }
						}
						<ul class="links">
							{d.links.map(x => <li>{x}</li>)}
						</ul>
					</div>
					)
				}
			case a : Alteracao => { val id = renderId(a.path) ; (
				<div class="alteracao" id={"div_" + id}>
				<ul class="alteracaoMatches"> { 
				  a.matches.map(m => <li>{m.toString}</li>)
				}
				</ul>
				
				{
					NodeSeq fromSeq a.blocks.zipWithIndex.flatMap(render(id))
				}															
				</div>) }
			case Omissis(abreAspas,fechaAspas,notaAlteracao) => <div class="omissis" id={"div_" + idPai + "_omi" + idx }>...</div>
			case Paragraph(ns,_) => <p class="texto">{NodeSeq fromSeq ns}</p>
			case Table(elem) => elem
			case x => <div class="erro">Elemento não esperado: {x.toString}</div> 
	   }
	}

	
	def renderArticulacao(blocks : List[Block]) : NodeSeq = <div class="articulacao" id="div_articulacao">{NodeSeq fromSeq blocks.zipWithIndex.flatMap(render(""))}</div>
  
	def renderParteInicial(pl : ProjetoLei) : NodeSeq = (
			<div class="parteInicial" id="div_parte_inicial">		
				<div class="epigrafe" id="div_epigrafe">
					<p class="rotuloElemento" id="rotulo_epigrafe">Epígrafe:</p>
					<p class="conteudoElemento" id="conteudo_epigrafe">{pl.epigrafe.toNodeSeq}</p>				
				</div>
            	<div class="ementa" id="div_ementa">
					<p class="rotuloElemento" id="rotulo_ementa">Ementa:</p>
					<p class="conteudoElemento" id="conteudo_ementa">{pl.ementa.toNodeSeq}</p>	
				</div>
				<div class="preambulo" id="div_preambulo">
					<p class="rotuloElemento" id="rotulo_preambulo">Preâmbulo:</p>
					<p class="conteudoElemento" id="conteudo_preambulo">{NodeSeq fromSeq pl.preambulo.flatMap(_.toNodeSeq)}</p>				
				</div>                    
            </div>
            )
          
    
         /* <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
				"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"> */
	def render(pl : ProjetoLei, headers : NodeSeq, bodyHead : NodeSeq, bodyTail : NodeSeq) : NodeSeq = (			
		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>				
				{headers}
			</head>
			<body>		
				{bodyHead}
				<div class="norma">			
					{renderParteInicial(pl)}
					{renderArticulacao(pl.articulacao)}					
				</div>		
				{bodyTail}
			</body>
		</html>
	) 
}